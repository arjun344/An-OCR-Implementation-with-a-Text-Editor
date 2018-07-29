package com.example.airgaz.scanner;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.List;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import io.github.mthli.knife.KnifeText;

import static android.content.ContentValues.TAG;

public class Tab1 extends Fragment {

    private static final String BOLD = "<b>Hey Ya...</b><br><br>";
    private static final String EXAMPLE = BOLD ;

    private KnifeText knife;
    View view;
    String k;
    String dirpath;
    private File pdfFile;
    float size;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       if(savedInstanceState == null)
        {
            if(getArguments()!=null)
            {
                k = this.getArguments().getString("textdata");

            }

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_tab1, container, false);
        knife = view.findViewById(R.id.knife);
        knife.setSelection(knife.getEditableText().length());
        knife.setText(k);
        setupBold();
        setupItalic();
        setupUnderline();
        setupSize();
        setupLink();
        setupClear();
        setupSave();
        return view;
    }

    private void setupBold() {
        final ImageButton bold = view.findViewById(R.id.bold);

        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                bold.startAnimation(myAnim);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                bold.startAnimation(myAnim);
                knife.bold(!knife.contains(KnifeText.FORMAT_BOLD));
            }
        });

        bold.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), R.string.toast_bold, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupItalic() {
        final ImageButton italic = view.findViewById(R.id.italic);

        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                italic.startAnimation(myAnim);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                italic.startAnimation(myAnim);
                knife.italic(!knife.contains(KnifeText.FORMAT_ITALIC));
            }
        });

        italic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), R.string.toast_italic, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupUnderline() {
        final ImageButton underline = view.findViewById(R.id.underline);

        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                underline.startAnimation(myAnim);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                underline.startAnimation(myAnim);
                knife.underline(!knife.contains(KnifeText.FORMAT_UNDERLINED));
            }
        });

        underline.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), R.string.toast_underline, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupSize() {
       final ImageButton quote = view.findViewById(R.id.fontsize);

        quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                quote.startAnimation(myAnim);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                quote.startAnimation(myAnim);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(false);

                View view = getLayoutInflater().inflate(R.layout.dialog_link, null, false);
                final EditText editText = (EditText) view.findViewById(R.id.edit);
                builder.setView(view);
                builder.setTitle("enter font size");
                editText.setHint(" ");

                builder.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        size=Float.parseFloat(editText.getText().toString());
                        knife.setTextSize(size);
                    }
                });

                builder.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // DO NOTHING HERE
                    }
                });

                builder.create().show();
            }
        });

        quote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), R.string.toast_quote, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupLink() {
        final ImageButton link =view.findViewById(R.id.link);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                link.startAnimation(myAnim);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                link.startAnimation(myAnim);
                showLinkDialog();
            }
        });

        link.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast.makeText(getContext(), R.string.toast_insert_link, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupClear() {
        final ImageButton clear = view.findViewById(R.id.clear);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                clear.startAnimation(myAnim);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                clear.startAnimation(myAnim);
                knife.clearFormats();
            }
        });

        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), R.string.toast_format_clear, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupSave() {
        final ImageButton save = view.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                save.startAnimation(myAnim);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                save.startAnimation(myAnim);
                try{
                    createPdf();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                Toast.makeText(getContext(),"save", Toast.LENGTH_SHORT).show();

            }
        });


        save.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast.makeText(getContext(), "Save as PDF", Toast.LENGTH_SHORT).show();

                return true;
            }
        });
    }

    private void showLinkDialog() {
        final int start = knife.getSelectionStart();
        final int end = knife.getSelectionEnd();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_link, null, false);
        final EditText editText = (EditText) view.findViewById(R.id.edit);
        builder.setView(view);
        builder.setTitle(R.string.dialog_title);

        builder.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String link = editText.getText().toString().trim();
                if (TextUtils.isEmpty(link)) {
                    return;
                }

                // When KnifeText lose focus, use this method
                knife.link(link, start, end);
            }
        });

        builder.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // DO NOTHING HERE
            }
        });

        builder.create().show();
    }

    private void createPdf() throws FileNotFoundException, DocumentException {

        Document doc = new Document(PageSize.A4);
        int n;

        try {
            File dir = new File(Environment.getExternalStorageDirectory()
                    .getPath(), "ScannerPDF");
            if(!dir.exists())
                dir.mkdirs();

            File file = new File(dir, System.currentTimeMillis() + "newFile.pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter pdfwriter=null;

            PdfWriter.getInstance(doc, fOut);
            //open the document
            doc.open();
            Font f = new Font(Font.FontFamily.TIMES_ROMAN, size, Font.NORMAL, BaseColor.BLACK);
            Editable e=knife.getText();
            String s2=Html.toHtml(e);
            Chunk c = new Chunk(" ", f);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Paragraph.ALIGN_MIDDLE);
            HTMLWorker htmlWorker = new HTMLWorker(doc);
            htmlWorker.parse(new StringReader(s2));
        } catch (DocumentException de) {
            Toast.makeText(getContext()," "+de, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext()," "+e, Toast.LENGTH_SHORT).show();
        }
        finally {
            doc.close();
        }

    }




    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);


    }
}

