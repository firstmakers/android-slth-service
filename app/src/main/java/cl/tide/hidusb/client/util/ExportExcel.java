package cl.tide.hidusb.client.util;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import cl.tide.hidusb.R;
import cl.tide.hidusb.service.storage.sqlite.Data;
import cl.tide.hidusb.service.storage.sqlite.Samples;

/**
 * Created by eDelgado on 22-09-14.
 */
public class ExportExcel {

    public final static String ROOT_DIRECTORY ="/SLTH/";
    private static  int cellTemp = -1;
    private static int cellLum = -1;
    private static int cellHum = -1;
    private static int cell = 0;

    public static File write( Samples mData, Context context) {
        Workbook workbook;
        cell = 0;

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            Toast.makeText(context, "La memoria externa no está disponible", Toast.LENGTH_SHORT).show();
            return null;
        }

        workbook = new HSSFWorkbook();

        Sheet sheet = workbook.createSheet("Muestras");
        Iterator<Data> iterator = mData.getData().iterator();

        int rowIndex = 0;
        //título del archivo;
        Row rowtitle = sheet.createRow(rowIndex++);
        Cell celltitle1 = rowtitle.createCell(0);
        celltitle1.setCellValue(context.getString(R.string.name));
        Cell celltitle2 = rowtitle.createCell(1);
        celltitle2.setCellValue(mData.getDate());
        //GeoReferencia Latitud
        Row rowGeoLat = sheet.createRow(rowIndex++);
        Cell celllat =rowGeoLat.createCell(0);
        celllat.setCellValue(context.getString(R.string.latitude));
        Cell cellGeo0 =rowGeoLat.createCell(1);
        cellGeo0.setCellValue(mData.getLatitude());
        //GeoReferencia Longitud
        Row rowGeoLon = sheet.createRow(rowIndex++);
        Cell cellGeolon = rowGeoLon.createCell(0);
        cellGeolon.setCellValue(context.getString(R.string.longitude));
        Cell cellGeo1 = rowGeoLon.createCell(1);
        cellGeo1.setCellValue(mData.getLatitude());

        rowIndex++;
        Row header = sheet.createRow(rowIndex++);
        //style header
        CellStyle cs = workbook.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        if(mData.getType().equals("0x87")){
            cellTemp = cell++;
            Cell temp = header.createCell(cellTemp);
            temp.setCellValue(context.getString(R.string.sensor_name_temp));
            temp.setCellStyle(cs);

            cellLum = cell++;
            Cell lum = header.createCell(cellLum);
            lum.setCellValue(context.getString(R.string.sensor_name_light));
            lum.setCellStyle(cs);

            cellHum = cell++;
            Cell hum = header.createCell(cellHum);
            hum.setCellValue(context.getString(R.string.sensor_name_hum));
            hum.setCellStyle(cs);
        }

        Cell fecha0 = header.createCell(cell);
        fecha0.setCellValue(context.getString(R.string.datetime));
        fecha0.setCellStyle(cs);


        while (iterator.hasNext()) {
            Data sample = iterator.next();
            Row row = sheet.createRow(rowIndex++);
            if(cellTemp > -1){
                Cell cell0 = row.createCell(cellTemp);
                cell0.setCellValue(sample.getTemperature());
            }
            if(cellLum> -1){
                Cell cell1 = row.createCell(cellLum);
                cell1.setCellValue(sample.getLight());
            }
            if(cellHum > -1){
                Cell cell1 = row.createCell(cellHum);
                cell1.setCellValue(sample.getHumidity());
            }

            Cell fecha = row.createCell(cell);
            fecha.setCellValue(sample.getDatetime());
        }

        String root_sd = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root_sd + ROOT_DIRECTORY);
        if(!myDir.exists())
            myDir.mkdirs();

        File file = new File(root_sd + ROOT_DIRECTORY, mData.getDate().replace(":","_")+".xls");
        FileOutputStream os = null;

        try {
            cellHum = -1;
            cellLum = -1;
            cellTemp = -1;
            os = new FileOutputStream(file);
            workbook.write(os);
            Toast.makeText(context, context.getString(R.string.write_file)+ " "+ file, Toast.LENGTH_SHORT).show();
            return file;
        } catch (IOException e) {
            Toast.makeText(context , context.getString(R.string.write_file_error) +" "+ file +" "+ e, Toast.LENGTH_SHORT).show();
            Log.e("Export" , e.toString());
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.write_file) +" "+ e.getMessage(), Toast.LENGTH_SHORT);
            Log.e("Export" , e.toString());
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
                Log.e("Export" , ex.toString());
            }
        }
        return null;
    }


    /**
     *  */
    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
}
