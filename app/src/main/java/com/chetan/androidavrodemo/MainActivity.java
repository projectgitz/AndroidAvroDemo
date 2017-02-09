package com.chetan.androidavrodemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chetan.androidavrodemo.model.Person;
import com.chetan.androidavrodemo.model.PersonV1;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText userNameEditText;
    private EditText passwordEditText;
    private Button submitBtn;
    private TextView outputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userNameEditText = (EditText) findViewById(R.id.userNameET);
        passwordEditText = (EditText) findViewById(R.id.pwdET);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        outputTextView = (TextView) findViewById(R.id.outputTV);
        submitBtn.setOnClickListener(this);
    }

    private byte[] writeData() throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().binaryEncoder(bout, null);
        DatumWriter<PersonV1> writer = new SpecificDatumWriter<PersonV1>(PersonV1.class);
        PersonV1 person = PersonV1.newBuilder().
                setUsername(userNameEditText.getText().toString()).
                setPassword(passwordEditText.getText().toString()).
                setJoinedOn(System.currentTimeMillis()).
                build();
        Log.d(TAG, "Person...." + person);
        writer.write(person, encoder);
        encoder.flush();
        return bout.toByteArray();
    }

    private Person readData(byte[] dataArray) throws IOException {
        Decoder decoder = DecoderFactory.get().binaryDecoder(dataArray, null);
        DatumReader<Person> reader = new SpecificDatumReader<Person>(PersonV1.SCHEMA$, Person.SCHEMA$);
        return reader.read(null, decoder);
    }

    @Override
    public void onClick(View v) {
        if (v == submitBtn) {
            try {
                // Write some PersonV1 objects to a byte array
                Log.d(TAG, "Writing data to byte[]");
                byte[] dataArray = new byte[0];
                dataArray = writeData();
                Log.d(TAG, String.format("Wrote %d bytes%n", dataArray.length));

                // Read in Person objects from the byte array of PersonV1 objects
                Log.d(TAG, "Reading data from byte[]");
                Person person = readData(dataArray);
                outputTextView.setText(person.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
