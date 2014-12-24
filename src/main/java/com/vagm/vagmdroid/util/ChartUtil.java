package com.vagm.vagmdroid.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Singleton;

import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Environment;
import android.util.SparseArray;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.activities.GraphicActivity.ScaleTimeSeries;
import com.vagm.vagmdroid.dto.LabelDTO;
import com.vagm.vagmdroid.service.ControllerInfoService;
import com.vagm.vagmdroid.service.LabelService;
import com.vagm.vagmdroid.service.PropertyService;

/**
 * The Class AChartUtil.
 * 
 * @author Roman_Konovalov
 */
@Singleton
public class ChartUtil {

    /**
     * LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ChartUtil.class);

    /**
     * COLORS.
     */
    private static final int[] CHART_COLORS = new int[] { Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN };

    /**
     * STYLES.
     */
    private static final PointStyle[] CHART_STYLES = new PointStyle[] { PointStyle.POINT, PointStyle.POINT, PointStyle.POINT, PointStyle.POINT };

    /**
     * LINE_WIDTH.
     */
    private static final float LINE_WIDTH = 3f;

    /**
     * AXES_COLOR.
     */
    private static final int AXES_COLOR = Color.BLACK;

    /**
     * LABELS_COLOR.
     */
    private static final int LABELS_COLOR = Color.BLACK;

    /** The Constant DATE_FORMAT_PATTERN. */
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd_HH-mm-ss";

    /** The Constant OUTFITS_EXTRACT_FILE_NAME. */
    private static final String CHART_FILE_NAME = "Chart";

    /**
     * controllerInfoService.
     */
    @Inject
    private ControllerInfoService controllerInfoService;

    /**
     * labelService.
     */
    @Inject
    private LabelService labelService;

    /**
     * context.
     */
    @Inject
    private Context context;

    /**
     * propertyService.
     */
    @Inject
    private PropertyService propertyService;

    /**
     * Sets Renderer.
     * 
     * @param renderer
     *            renderer
     * @param chartTitle
     *            chartTitle
     * @param xTitle
     *            xTitle
     * @param length
     *            length
     */
    public void setRenderer(final XYMultipleSeriesRenderer renderer, final String chartTitle, final String xTitle, final int length) {
        renderer.setChartTitle(chartTitle);
        renderer.setXTitle(xTitle);
        renderer.setYAxisAlign(Align.RIGHT, 1);
        renderer.setYLabelsAlign(Align.LEFT, 1);
        renderer.setYLabelsAlign(Align.RIGHT, 0);
        renderer.setAxesColor(AXES_COLOR);
        renderer.setLabelsColor(LABELS_COLOR);
        renderer.setYLabelsColor(0, LABELS_COLOR);
        renderer.setYLabelsColor(1, LABELS_COLOR);
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setPointSize(5f);
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(context.getResources().getColor(R.color.mainBackground));
        renderer.setMarginsColor(context.getResources().getColor(R.color.mainBackground));
        renderer.setFitLegend(true);
        // renderer.setMargins(new int[] {20, 30, 0, 20 });
        for (int i = 0; i < length; i++) {
            final XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(CHART_COLORS[i]);
            r.setPointStyle(CHART_STYLES[i]);
            r.setLineWidth(LINE_WIDTH);
            renderer.addSeriesRenderer(r);
        }
    }

    /**
     * saveChart.
     * 
     * @param timeSeries
     *            timeSeries
     * @param blocksCount
     *            blocksCount
     * @return true if saved
     */
    public boolean saveChart(final ScaleTimeSeries[] timeSeries, final int blocksCount) {
        FileOutputStream fos = null;
        InputStream inputStream = null;
        try {
            SparseArray<LabelDTO> labels = labelService.getLabels();
            inputStream = PropertyService.class.getClassLoader().getResourceAsStream("assets" + File.separator + "templates/template.xls");
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);

            // HSSFSheet firstSheet =
            // workbook.createSheet(labels.get(controllerInfoService.getGroup(),
            // new LabelDTO(context)).getTitle());
            HSSFSheet dataSheet = workbook.getSheetAt(0);

            HSSFCellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setWrapText(true);
            titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            titleStyle.setFillBackgroundColor(new HSSFColor.RED().getIndex());

            HSSFRow controllerInfoRow = dataSheet.getRow(0);
            StringBuilder builder = new StringBuilder();
            builder.append(context.getString(R.string.VAGnumber) + controllerInfoService.getVagNumber());
            builder.append("\n");
            builder.append(context.getString(R.string.component) + controllerInfoService.getComponent());
            controllerInfoRow.getCell(0).setCellValue(new HSSFRichTextString(builder.toString()));

            HSSFRow groupTitleRow = dataSheet.getRow(1);
            groupTitleRow.getCell(0).setCellValue(
                    new HSSFRichTextString(labels.get(controllerInfoService.getGroup(), new LabelDTO(context)).getTitle()));

            HSSFRow titleRow = dataSheet.getRow(2);
            HSSFCell cell = titleRow.getCell(0);
            cell.setCellValue(new HSSFRichTextString(context.getString(R.string.x_axis_title)));

            for (int i = 0; i < blocksCount; i++) {
                cell = titleRow.getCell(i + 1);
                cell.setCellValue(new HSSFRichTextString(labels.get(controllerInfoService.getGroup(), new LabelDTO(context)).getGroup()[i]
                        .getTitle()));
            }

            for (int i = 0; i < timeSeries[0].getItemCount(); i++) {
                HSSFRow row = dataSheet.createRow(i + 3);
                row.createCell(0).setCellValue(timeSeries[0].getDate(i));

                for (int j = 0; j < blocksCount; j++) {
                    row.createCell(j + 1).setCellValue(timeSeries[j].getY(i));
                }
            }

            String time = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault()).format(new Date());
            String fileName = CHART_FILE_NAME + "_" + time + ".xls";

            File file = getFileToSave(fileName);
            
            if (file == null) {
                return false;
            }
            
            fos = new FileOutputStream(file);
            workbook.write(fos);

            LOG.debug("Chart saved to: {}", file.getAbsolutePath());
            return true;

        } catch (final IOException ex) {
            LOG.error("Cannot save chart", ex);
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    private File getFileToSave(String fileName) throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + propertyService.getAppName() + File.separator
                + propertyService.getSavedChartsFolder());
        if (!dir.exists() && !dir.mkdirs()) {
            LOG.error("Cannot create directory: {}", dir.getAbsolutePath());
            return null;
        }
        File file = new File(dir + File.separator + fileName);
        if (!file.createNewFile()) {
            LOG.error("Cannot create file: {}", file.getAbsolutePath());
            return null;
        }
        return file;
    }

}
