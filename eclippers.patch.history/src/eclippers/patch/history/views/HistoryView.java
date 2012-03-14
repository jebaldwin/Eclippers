package eclippers.patch.history.views;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.ViewContentProvider;
import org.eclipse.ui.part.ViewPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eclippers.patch.history.Activator;

import textmarker.parse.ParseXMLForMarkers;

public class HistoryView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "eclippers.patch.history.views.HistoryView";

	private TableViewer viewer;
	private Action action1;
	public static Table table;
	public static String patchPrefix = ".lecode.git";

	// Set column names
	private String[] columnNames = new String[] { "Project", "Patch Name", "Date Applied" };

	/**
	 * The constructor.
	 */
	public HistoryView() {

	}

	/**
	 * Create the Table
	 */
	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		final int NUMBER_COLUMNS = 3;

		table = new Table(parent, style);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// 1st column with image/checkboxes - NOTE: The SWT.CENTER has no
		// effect!!
		TableColumn column = new TableColumn(table, SWT.CENTER, 0);
		column.setText("Project");
		column.setWidth(300);

		// 2nd column with task Description
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Patch Name");
		column.setWidth(300);
		// Add listener to column so tasks are sorted by description when
		// clicked
		/*
		 * column.addSelectionListener(new SelectionAdapter() {
		 * 
		 * public void widgetSelected(SelectionEvent e) {
		 * tableViewer.setSorter(new
		 * ExampleTaskSorter(ExampleTaskSorter.DESCRIPTION)); } });
		 */

		// 3rd column with task Description
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Date Applied");
		column.setWidth(300);
		// Add listener to column so tasks are sorted by description when
		// clicked
		/*
		 * column.addSelectionListener(new SelectionAdapter() {
		 * 
		 * public void widgetSelected(SelectionEvent e) {
		 * tableViewer.setSorter(new
		 * ExampleTaskSorter(ExampleTaskSorter.DESCRIPTION)); } });
		 */
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(new ChangeHistoryListener());

		createTable(parent);
		// viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL |
		// SWT.V_SCROLL);
		viewer = new TableViewer(table);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		// viewer.setSorter(new NameSorter());
		// viewer.setInput(getViewSite());
		viewer.setColumnProperties(columnNames);

		IProject proj = getSelectedProject();
		populate(proj);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "eclippers.patch.history.viewer");
		// makeActions();
		// hookContextMenu();
		hookDoubleClickAction();
	}

	public static void populate(IProject proj) {
		if(proj == null)
			return;
		
		File xmlFile = new File(proj.getLocation() + File.separator + patchPrefix + File.separator + "patch.cfg");
		table.removeAll();
		
		if (xmlFile.exists()) {

			try {

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder;
				builder = factory.newDocumentBuilder();
				Document document = builder.parse(xmlFile);
				String patchName = "";

				// get the root element
				Element rootElement = document.getDocumentElement();

				// get each patch element
				NodeList nl = rootElement.getElementsByTagName("patch");
				if (nl != null && nl.getLength() > 0) {
					for (int i = 0; i < nl.getLength(); i++) {
						Element patchElement = (Element) nl.item(i);
						patchName = patchElement.getAttribute("name");
						String applied = patchElement.getAttribute("applied");
						String date = patchElement.getAttribute("date");

						TableItem item = new TableItem(table, 0);
						item.setText(new String[] { proj.getName(), patchName, date });
					}
				}

			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private IProject getSelectedProject() {
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection("org.eclipse.jdt.ui.PackageExplorer");
		if (selection instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) selection).getFirstElement();
			if (selected instanceof IJavaProject)
				return ((IJavaProject) selected).getProject();
			else if (selected instanceof IResource) {
				IResource resource = (IResource) selected;
				return resource.getProject();
			} else if (selected instanceof ICompilationUnit) {
				ICompilationUnit cu = (ICompilationUnit) selected;
				return cu.getJavaProject().getProject();
			}
		}
		return null;
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				HistoryView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
				// ParseXMLForMarkers.parseXML(proj, null, ".lecode.git", null);
			}
		};
		action1.setText("Show Diff Inline");
		action1.setToolTipText("This will show the changes to the code within the editors and package explorer.");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				// doubleClickAction.run();
				// action1.run();
				TableItem[] items = table.getSelection();
				TableItem item = items[0];
				String project = item.getText(0);

				String patchName = item.getText(1);
				// ParseXMLForMarkers.parseXML(proj, null, ".lecode.git",
				// brickName);

				//showMessage(project + " " + patchName);
				IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
				ParseXMLForMarkers.parseXML(proj, null, ".lecode.git", patchName);
				
				//TODO JB: auto expand project one level
				
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Patch History View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}