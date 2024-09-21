package com.example.skinalert;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.skinalert.ml.SkinCancerModel;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class ClassificationFragment extends Fragment {

    private ImageView imageView;
    private TextView resultTextView;
    private Bitmap capturedImage;
    private ArrayList<String> labels = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classification, container, false);

        imageView = view.findViewById(R.id.imageView);
        resultTextView = view.findViewById(R.id.resultTextView);
        Button classifyButton = view.findViewById(R.id.classifyButton);
        Button homeButton = view.findViewById(R.id.homeButton);

        loadLabels();

        Bundle bundle = getArguments();
        if (bundle != null) {
            capturedImage = bundle.getParcelable("capturedImage");
            if (capturedImage != null) {
                imageView.setImageBitmap(capturedImage);
            } else {
                Toast.makeText(getContext(), "Erro: Imagem não disponível", Toast.LENGTH_SHORT).show();
                return view;
            }
        }

        classifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (capturedImage != null) {
                    classifyImage(capturedImage);
                } else {
                    Toast.makeText(getContext(), "Erro: Imagem não encontrada para classificação", Toast.LENGTH_SHORT).show();
                }
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        return view;
    }

    private void loadLabels() {
        try {
            InputStream inputStream = getContext().getAssets().open("labels.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                labels.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao carregar rótulos", Toast.LENGTH_SHORT).show();
        }
    }

    private void classifyImage(Bitmap image) {
        try {
            SkinCancerModel model = SkinCancerModel.newInstance(getContext());

            Bitmap resizedImage = Bitmap.createScaledBitmap(image, 224, 224, true);

            ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
            inputBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[224 * 224];
            resizedImage.getPixels(intValues, 0, 224, 0, 0, 224, 224);

            int pixel = 0;
            for (int i = 0; i < 224; ++i) {
                for (int j = 0; j < 224; ++j) {
                    final int val = intValues[pixel++];
                    inputBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
                    inputBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
                    inputBuffer.putFloat((val & 0xFF) / 255.0f);
                }
            }

            TensorBuffer inputFeature = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            inputFeature.loadBuffer(inputBuffer);

            SkinCancerModel.Outputs outputs = model.process(inputFeature);
            TensorBuffer outputFeature = outputs.getOutputFeature0AsTensorBuffer();

            float[] probabilities = outputFeature.getFloatArray();
            int maxIndex = getMaxIndex(probabilities);

            if (!labels.isEmpty() && labels.size() > 1) {
                float confidence = probabilities[maxIndex] * 100;

                if (labels.get(maxIndex).equals("desconhecido")) {
                    probabilities[maxIndex] = 0;
                    maxIndex = getMaxIndex(probabilities);
                    confidence = probabilities[maxIndex] * 100;
                }

                if (confidence < 85) {
                    resultTextView.setText("nenhum câncer foi detectado!");
                } else {
                    resultTextView.setText(labels.get(maxIndex));
                }
            } else {
                resultTextView.setText("Resultado não disponível (rótulos não carregados ou incompletos).");
            }

            model.close();
        } catch (IOException e) {
            e.printStackTrace();
            resultTextView.setText("Erro ao carregar o modelo.");
        } catch (Exception e) {
            e.printStackTrace();
            resultTextView.setText("Erro durante a classificação.");
        }
    }

    private int getMaxIndex(float[] arr) {
        int maxIndex = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
