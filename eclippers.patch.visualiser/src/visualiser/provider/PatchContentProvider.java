package visualiser.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.contribution.visualiser.VisualiserPlugin;
import org.eclipse.contribution.visualiser.core.ProviderManager;
import org.eclipse.contribution.visualiser.interfaces.IGroup;
import org.eclipse.contribution.visualiser.interfaces.IMarkupProvider;
import org.eclipse.contribution.visualiser.interfaces.IMember;
import org.eclipse.contribution.visualiser.simpleImpl.SimpleContentProvider;
import org.eclipse.contribution.visualiser.simpleImpl.SimpleGroup;
import org.eclipse.contribution.visualiser.simpleImpl.SimpleMarkupProvider;
import org.eclipse.contribution.visualiser.simpleImpl.SimpleMember;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import visualiser.convertxml.ConvertXMLtoMVIS;

public class PatchContentProvider extends SimpleContentProvider implements ISelectionListener {

	private static String WORKSPACE_ROOT = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
	private final static boolean debugLoading = false;
	private IProject lastProj = null;

	/**
	 * Initialise the provider - reads in the information from a file
	 */
	public void initialise() {

		if (VisualiserPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow() != null) {
			VisualiserPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(this);
		}
	}

	/**
	 * Log the given message
	 * 
	 * @param msgType
	 * @param msg
	 * @param e
	 */
	public static void log(int msgType, String msg, Exception e) {
		// An example of how to send log data to the .metadata/.log file.
		VisualiserPlugin.getDefault().getLog().log(new Status(msgType, "org.eclipse.contribution.visualiser", 0, msg, e)); //$NON-NLS-1$
	}

	/**
	 * Loads the information for a visualisation from an input stream. Entries
	 * in the file are either of the form: Group:XX or Member:Y [Size:NNN]
	 * [Tip:SSSS]
	 * 
	 * A member entry must be after a Group entry, and the member is considered
	 * a member of that group.
	 * 
	 * @param in
	 *            input stream
	 */
	public void loadVisContents(InputStream in) {

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = br.readLine();
			IGroup grp = null;
			while (line != null && line.length() != 0) {
				if (line.startsWith("Group:")) { //$NON-NLS-1$
					// Once a group tag is found, all following members are
					// considered to be in that group
					String grpname = retrieveKeyValue("Group:", line); //$NON-NLS-1$
					grp = new SimpleGroup(grpname);
					addGroup(grp);

				} else if (line.startsWith("Member:")) { //$NON-NLS-1$
					String memname = retrieveKeyValue("Member:", line); //$NON-NLS-1$
					IMember mem = new SimpleMember(memname);

					// Size might not be specified, so don't try parsing a null
					// into an int !
					String sizeStr = retrieveKeyValue("Size:", line); //$NON-NLS-1$			
					if (sizeStr != null)
						mem.setSize(Integer.parseInt(sizeStr));

					String tipStr = retrieveKeyValue("Tip:", line); //$NON-NLS-1$
					if (tipStr != null)
						mem.setTooltip(tipStr);

					grp.add(mem);
				}
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log(IStatus.ERROR, "FileContentProvider failed to load file (FNF)", e); //$NON-NLS-1$
		} catch (IOException e) {
			e.printStackTrace();
			log(IStatus.ERROR, "FileContentProvider failed to load file (FNF)", e); //$NON-NLS-1$
		}
	}

	/**
	 * Given a 'key' it looks for the key in a supplied string and returns the
	 * value after the key. For example, looking for "Fred:" in the string
	 * "Barney:40 Fred:45 Betty:40" would return "45". If values need to have
	 * spaces in then _ characters can be used, this method will translate those
	 * to spaces before it returns.
	 * 
	 * @param what
	 *            The key to look for
	 * @param where
	 *            The string to locate the key in
	 * @return the value after the key (whitespace is the value delimiter)
	 */
	private static String retrieveKeyValue(String what, String where) {
		if (debugLoading)
			System.err.println("looking for '" + what + "' in '" + where + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (where.indexOf(what) == -1)
			return null;
		String postWhat = where.substring(where.indexOf(what) + what.length());
		String result = postWhat;
		if (result.indexOf(" ") != -1)result = postWhat.substring(0, postWhat.indexOf(" ")); //$NON-NLS-1$ //$NON-NLS-2$
		result = result.replace('_', ' ');
		if (debugLoading)
			System.err.println("Returning '" + result + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		return result;
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IProject proj = null;
		// if the Visualiser is showing, update to use the new settings
		if (VisualiserPlugin.visualiser != null) {
			if (selection instanceof IStructuredSelection) {
				Object selected = ((IStructuredSelection) selection).getFirstElement();

				if (selected instanceof IResource) {
					IResource resource = (IResource) selected;
					proj = resource.getProject();
				} else if (selected instanceof IJavaProject) {
					proj = ((IJavaProject) selected).getProject();
				} else if (selected instanceof IResource) {
					IResource resource = (IResource) selected;
					proj = resource.getProject();
				} else if (selected instanceof ICompilationUnit) {
					ICompilationUnit cu = (ICompilationUnit) selected;
					proj = cu.getJavaProject().getProject();
				} else {
					try {
						IFile file = (IFile) part.getSite().getWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
						proj = file.getProject();
					} catch(NullPointerException npe){
						//do nothing
					}
				}
			} else if (selection instanceof TextSelection) {
				IFile file = (IFile) part.getSite().getWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
				proj = file.getProject();
			}

			if (proj != lastProj) {

				super.resetModel();
				lastProj = proj;

				ConvertXMLtoMVIS.convertContentVis(proj);
				ConvertXMLtoMVIS.convertMarkupVis(proj);

				if (numberOfGroupsDefined() == 0) {
					try {
						File fileURL = new File(proj.getLocation() + File.separator + "Content.vis");
						InputStream in = new FileInputStream(fileURL);
						loadVisContents(in);
						in.close();
					} catch (IOException ioe) {
						ioe.printStackTrace();
						VisualiserPlugin.logException(ioe);
					}

					IMarkupProvider markupP = ProviderManager.getMarkupProvider();
					if (markupP instanceof SimpleMarkupProvider) {
						((SimpleMarkupProvider) markupP).resetColours();
					}

					if (VisualiserPlugin.menu != null) {
						VisualiserPlugin.menu.setVisMarkupProvider(markupP);
					}
					VisualiserPlugin.visualiser.updateDisplay(true);
				}
			}
		}
	}
}
