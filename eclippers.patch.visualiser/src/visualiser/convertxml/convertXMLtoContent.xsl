<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">
 
<xsl:template match="patchdata">
	<xsl:for-each select="patch/file">
Group:<xsl:value-of select="@package"/>
Member:<xsl:value-of select="@name"/> Size:<xsl:value-of select="@length"/> Tip:<xsl:value-of select="@package"/>.<xsl:value-of select="@name"/>.java</xsl:for-each>
</xsl:template>
   
</xsl:stylesheet>