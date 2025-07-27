package com.softjk.waitapp.Sistema.Metodos;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.softjk.waitapp.Cliente.E1_Sala_Client;
import com.softjk.waitapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TarjetaPDF {
    private Context context;
    TextView Negocio, CodNeg;
    ImageView LogoNeg;

    public TarjetaPDF(Context context){
        this.context = context;
    }

    public Bitmap generarVista(String Nombre, String Logo, String codigo) throws IOException {
        LayoutInflater inflater = LayoutInflater.from(context);
        View vista = inflater.inflate(R.layout.z_custom_dpf,null);

        Negocio = vista.findViewById(R.id.PDF_Neg);
        CodNeg = vista.findViewById(R.id.PDF_Codig);
        LogoNeg = vista.findViewById(R.id.PDF_Img);

        Glide.with(context).load(Logo).circleCrop().into(LogoNeg);
        Negocio.setText(Nombre);
        CodNeg.setText("Código: "+codigo);

        int ancho = (int)(10 * 300 / 2.54); // 10 cm
        int alto  = (int)(10 * 300 / 2.54); // 10 cm

        int widthSpec = View.MeasureSpec.makeMeasureSpec(ancho,View.MeasureSpec.EXACTLY); //18 cm
        int heigthSpec = View.MeasureSpec.makeMeasureSpec(alto, View.MeasureSpec.EXACTLY); // 23 cm
        vista.measure(widthSpec,heigthSpec);
        vista.layout(0,0, ancho, alto);

        Bitmap bitmap = Bitmap.createBitmap(vista.getMeasuredWidth(), vista.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vista.draw(canvas);

        generarBitMap(bitmap, context);
        guardarPDF(bitmap);

        return bitmap;
    }


    public void generarBitMap(Bitmap bitmap, Context context) throws IOException {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap,0,0,null);
        document.finishPage(page);

        File file = new File(context.getApplicationContext().getExternalFilesDir(null),"MiCodigo.pdf");
        FileOutputStream stream = new FileOutputStream(file);
        document.writeTo(stream);
        document.close();
        stream.close();

    }


    private void guardarPDF(Bitmap bitmap) {
        ContentValues valores = new ContentValues();
        valores.put(MediaStore.MediaColumns.DISPLAY_NAME, "MiCodigo.pdf");
        valores.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        valores.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/WaitApp");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), valores);

        try (OutputStream out = resolver.openOutputStream(uri)) {
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            page.getCanvas().drawBitmap(bitmap, 0, 0, null);
            document.finishPage(page);
            document.writeTo(out);
            document.close();

        } catch (IOException e) {
            e.printStackTrace(); // O puedes lanzar un Toast o mensaje de error
        }
    }


}
