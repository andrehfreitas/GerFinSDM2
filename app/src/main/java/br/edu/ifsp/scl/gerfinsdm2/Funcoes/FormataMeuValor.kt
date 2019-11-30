package br.edu.ifsp.scl.gerfinsdm2.Funcoes

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

//Créditos no link: https://weeklycoding.com/mpandroidchart-documentation/formatting-data-values/

class FormataMeuValor : ValueFormatter() {
    private val format = DecimalFormat("R$ ###,##0.0")
    // override this for e.g. LineChart or ScatterChart
    override fun getPointLabel(entry: Entry?): String {
        return format.format(entry?.y)
    }
    // override this for BarChart
    override fun getBarLabel(barEntry: BarEntry?): String {
        return format.format(barEntry?.y)
    }
    // override this for custom formatting of XAxis or YAxis labels
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        axis?.setLabelCount(6, true) // Quantidade de linhas na escala
        return format.format(value)
    }
}
