<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">

<xsl:template match="globalPatchData">
   <xsl:for-each select="patch">
   	<xsl:variable name="patchName" select="@name"/>
   	<xsl:for-each select="file">
   		<xsl:variable name="fileName" select="@name"/>
   		<xsl:variable name="packageName" select="@package"/>
   		<xsl:for-each select="offset">
Stripe:<xsl:value-of select="$packageName"/>.<xsl:value-of select="$fileName"/> Kind:<xsl:value-of select="$patchName"/> Offset:<xsl:value-of select="@start"/> Depth:<xsl:value-of select="@length"/>
   		</xsl:for-each>
   	</xsl:for-each>
   </xsl:for-each>
</xsl:template>
       
</xsl:stylesheet>