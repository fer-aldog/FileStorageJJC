package org.pattersonclippers.filestoragejjc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/*notes:
    logcat = log, concatenate
    json = javascript objects notation
 */
public class MainActivity extends AppCompatActivity {

    int index;
    EditText typeET;
    TextView contactName, contactInfo;
    Button contactBTN;
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        index = 0;
        typeET = (EditText) findViewById(R.id.typeET);
        contactName = (TextView) findViewById(R.id.contactName);
        contactInfo = (TextView) findViewById(R.id.contactInfo);
        contactBTN = (Button) findViewById(R.id.contactBTN);
        contactList = new ArrayList<>();

        contactBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //createFile();
                    //readFile();

                    String assetContents = readAsset("addressbook.json");
                    processJSON(assetContents);
                    showContact(contactBTN);

                } catch (IOException | JSONException e) {
                    Log.e("youdidnthelpme", "Exception");
                    e.printStackTrace();
                }
            }
        });
    }

    protected void createFile() throws IOException {
        //creates an empty file within our app's storage
        FileOutputStream fOut = openFileOutput("myFile.txt", Context.MODE_PRIVATE);

        //string to use as contents of our file
        String myString = "This is a test of file storage.";

        //convert string into bytes (8-bits of 0 or 1) and write to the file
        fOut.write(myString.getBytes());

        //end connection to file to save memory
        fOut.close();
    }

    protected void readFile() throws IOException {
        //open the file for input
        FileInputStream fIn = openFileInput("myFile.txt");

        //temporary variable to store contents of file
        String temp = "";

        //while loop reads through every byte of the file, convert to char, then append to temp
        int c = fIn.read();
        while(c != -1) {
            temp += Character.toString((char) c);
            c = fIn.read();
        }

        // log our file contents to the debugger logcat
        Log.d("HEEEELPPPPPP", "Your file contents were:");
        Log.d("HEEEELPPPPPP", temp);

        //close file
        fIn.close();

    }

    protected String readAsset(String fileName) throws IOException {
        //create temp string to hold results in file
        String results = "";

        /*open an input stream and use getAssets() to access the assets folder,
          a special folder provided by Android */

        InputStream inputStream = getAssets().open(fileName);

        //get the file size (number of bytes)
        int size = inputStream.available();

        byte[] buffer = new byte[size];

        //read the bytes of our file stream and copy into the byte array named buffer
        inputStream.read(buffer);

        //convert buffer to a string
        results = new String(buffer);

        //check if it works
        Log.d("Checking asset reading", results);
        return results;
    }

    /* This method takes in a string containing JSON
    * and adds each contact to our contactList.
    *
    * */
    protected void processJSON(String jsonString) throws JSONException {
        //get top-level object and process rest of tree
        JSONObject root = new JSONObject(jsonString);

        //get the 'contacts' [] array from JSON and treat as a array
        JSONArray contacts = root.getJSONArray("contacts");

        //traverse the contacts array
        for(int i = 0; i < contacts.length(); i++) {

            // JSON objects are delineated in curly braces so
            //we retrieve each contact as its own object
            JSONObject contact = contacts.getJSONObject(i);

            //because it is a JSON object, we can get its properties so
            //here, we are getting the value paired with the 'name' key
            String name = contact.getString("name");

            //get phone numbers
            JSONObject contactPhones = contact.getJSONObject("phones");
            String personalPhone = contactPhones.getString("personal");
            String workPhone = contactPhones.getString("work");

            //create temporary key/value hashmap for a single contact
            HashMap<String, String> newContact = new HashMap<>();
            newContact.put("name", name);
            newContact.put("personal", personalPhone);
            newContact.put("work", workPhone);

            //add contact to the contact list
            contactList.add(newContact);
        }
    }

    public void showContact(View view) {
        contactInfo.setText("woop woop :D");

        //make a random index by casting
        index = (int) (Math.random() * contactList.size());

        //get a (random) contact using our index
        HashMap<String, String> randomContact = contactList.get(index);

        //create a String template message
        String contactMessage = "Maybe you should give %s a call.\nTheir phone number " +
                "is %s.\nTheir work number is %s.";

        //fill in the 'blanks' (%s) of the template using randomContact's info
        contactMessage = String.format(contactMessage, randomContact.get("name"),
                randomContact.get("personal"), randomContact.get("work"));

        //set textview to show the string
        contactInfo.setText(contactMessage);
    }


}