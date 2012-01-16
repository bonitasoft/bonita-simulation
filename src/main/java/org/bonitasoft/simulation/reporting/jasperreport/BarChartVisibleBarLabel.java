/**
 * Copyright (C) 2010 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.simulation.reporting.jasperreport;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.ui.TextAnchor;

/**
 * @author Romain Bioteau
 *
 */
public class BarChartVisibleBarLabel implements JRChartCustomizer {

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.JRChartCustomizer#customize(org.jfree.chart.JFreeChart, net.sf.jasperreports.engine.JRChart)
	 */
	public void customize(JFreeChart chart, JRChart jasperChart) {
		
		BarRenderer bsr = (BarRenderer) chart.getCategoryPlot().getRenderer() ;
		bsr.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		bsr.setItemLabelsVisible(true);
		// Set position of Items label : center
		bsr.setPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		bsr.setNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		// If there isn't enough space to draw the ItemLabel...
		bsr.setPositiveItemLabelPositionFallback(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER));
		bsr.setNegativeItemLabelPositionFallback(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER));
	}

}
