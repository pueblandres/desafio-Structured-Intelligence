<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/declaracion">
        <declaracionInterna>
            <numeroDespacho>
                <xsl:value-of select="numeroDespacho"/>
            </numeroDespacho>
            <fechaEmision>
                <xsl:value-of select="fechaEmision"/>
            </fechaEmision>
            <cuitImportador>
                <xsl:value-of select="importador/cuit"/>
            </cuitImportador>
            <moneda>
                <xsl:value-of select="moneda"/>
            </moneda>
            <totalFOB>
                <xsl:call-template name="sum-items">
                    <xsl:with-param name="items" select="items/item"/>
                    <xsl:with-param name="total" select="0"/>
                </xsl:call-template>
            </totalFOB>
            <estado>RECIBIDA</estado>
            <items>
                <xsl:for-each select="items/item">
                    <item>
                        <ncm><xsl:value-of select="ncm"/></ncm>
                        <descripcion><xsl:value-of select="descripcion"/></descripcion>
                        <cantidad><xsl:value-of select="cantidad"/></cantidad>
                        <valorUnitario><xsl:value-of select="valorUnitario"/></valorUnitario>
                    </item>
                </xsl:for-each>
            </items>
        </declaracionInterna>
    </xsl:template>

    <xsl:template name="sum-items">
        <xsl:param name="items"/>
        <xsl:param name="total"/>
        <xsl:choose>
            <xsl:when test="count($items) = 0">
                <xsl:value-of select="format-number($total, '0.00')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="sum-items">
                    <xsl:with-param name="items" select="$items[position() &gt; 1]"/>
                    <xsl:with-param name="total" select="$total + (number($items[1]/cantidad) * number($items[1]/valorUnitario))"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
