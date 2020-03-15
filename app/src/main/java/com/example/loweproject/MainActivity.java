package com.example.loweproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class MainActivity extends ListActivity{
    private List<CountryItem> countryList;
    private static int SPLASH_TIME_OUT=4000;
    /** Items entered by the user is stored in this ArrayList variable */
    ArrayList<String> listArray = new ArrayList<String>();

    /** Declaring an ArrayAdapter to set items to ListView */
    ArrayAdapter<String> adapter2;

    String item,product_name;
    String var_rack_order = "";
    private static final String TAG = MainActivity.class.getName();

    int i,j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillCountryList();

        AutoCompleteTextView editText = findViewById(R.id.actv);
        AutoCompleteCountryAdapter adapter = new AutoCompleteCountryAdapter(this, countryList);
        editText.setAdapter(adapter);
        /** Reference to the button of the layout main.xml */
        Button btn = (Button) findViewById(R.id.btnAdd);

        /** Reference to the delete button of the layout main.xml */
        Button btnDel = (Button) findViewById(R.id.btnDel);

        final Button btnDone = (Button) findViewById(R.id.btnDone);

        /** Defining the ArrayAdapter to set items to ListView */
        adapter2= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, listArray);

        /** Defining a click event listener for the button "Add" */
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView edit = (AutoCompleteTextView) findViewById(R.id.actv);
                item = edit.getText().toString();
                int var_toast = 0;
                for(CountryItem country:countryList)
                {
                    if(country.getCountryName().equals(item))
                    {
                        listArray.add(item);
                        var_toast = 0;
                        break;
                    }
                    else{
                        var_toast = 1;
                    }
                }
                if(var_toast == 1) {
                    Toast.makeText(MainActivity.this, "Enter Valid Option!", Toast.LENGTH_SHORT).show();
                }


                //Deleting Same entries

                HashSet<String> hashSet = new HashSet<String>();
                hashSet.addAll(listArray);
                listArray.clear();
                listArray.addAll(hashSet);

                //Alphabetic sorting.
                Collections.sort(listArray);
                edit.setText("");
                adapter2.notifyDataSetChanged();
            }
        };

        /** Defining a click event listener for the button "Delete" */
        View.OnClickListener listenerDel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Getting the checked items from the listview */
                SparseBooleanArray checkedItemPositions = getListView().getCheckedItemPositions();
                int itemCount = getListView().getCount();
                int item_flag = 0;

                if(itemCount == 0) {
                    Toast.makeText(MainActivity.this, "List is Empty!", Toast.LENGTH_SHORT).show();
                    item_flag = 1;
                }
                for(int i=itemCount-1; i >= 0; i--){
                    if(checkedItemPositions.get(i)){
                        adapter2.remove(listArray.get(i));
                        item_flag = 1;
                    }
                }
                if(item_flag == 0){
                    Toast.makeText(MainActivity.this, "Select Items to delete!", Toast.LENGTH_SHORT).show();
                }
                else{
                    item_flag = 0;
                }
                checkedItemPositions.clear();
                adapter2.notifyDataSetChanged();
            }
        };

        /** Defining a click event listener for the button "Done" */
        View.OnClickListener listenerDone = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if(listArray.size() == 0)
                        Toast.makeText(MainActivity.this, "List is empty!", Toast.LENGTH_SHORT).show();

                    int[][] distanceMatrix = {
                            {0, 2451, 713, 1018, 1631, 1374, 2408, 213, 2571, 875, 1420, 2145, 1972},
                            {2451, 0, 1745, 1524, 831, 1240, 959, 2596, 403, 1589, 1374, 357, 579},
                            {713, 1745, 0, 355, 920, 803, 1737, 851, 1858, 262, 940, 1453, 1260},
                            {1018, 1524, 355, 0, 700, 862, 1395, 1123, 1584, 466, 1056, 1280, 987},
                            {1631, 831, 920, 700, 0, 663, 1021, 1769, 949, 796, 879, 586, 371},
                            {1374, 1240, 803, 862, 663, 0, 1681, 1551, 1765, 547, 225, 887, 999},
                            {2408, 959, 1737, 1395, 1021, 1681, 0, 2493, 678, 1724, 1891, 1114, 701},
                            {213, 2596, 851, 1123, 1769, 1551, 2493, 0, 2699, 1038, 1605, 2300, 2099},
                            {2571, 403, 1858, 1584, 949, 1765, 678, 2699, 0, 1744, 1645, 653, 600},
                            {875, 1589, 262, 466, 796, 547, 1724, 1038, 1744, 0, 679, 1272, 1162},
                            {1420, 1374, 940, 1056, 879, 225, 1891, 1605, 1645, 679, 0, 1017, 1200},
                            {2145, 357, 1453, 1280, 586, 887, 1114, 2300, 653, 1272, 1017, 0, 504},
                            {1972, 579, 1260, 987, 371, 999, 701, 2099, 600, 1162, 1200, 504, 0} };

                    InputStream is = getAssets().open("array.txt");
                    InputStream is2 = getAssets().open("arrayrack.txt");
                    InputStream is3 = getAssets().open("path.txt");

                    // We guarantee that the available method returns the total
                    // size of the asset...  of course, this does mean that a single
                    // asset can't be more than 2 gigs.
                    int size1 = is.available();
                    int size2 = is2.available();
                    int size3 = is3.available();

                    // Read the entire asset into a local byte buffer.
                    byte[] buffer_product = new byte[size1];
                    is.read(buffer_product);
                    is.close();

                    byte[] buffer_rack = new byte[size2];
                    is2.read(buffer_rack);
                    is2.close();

                    byte[] buffer_path = new byte[size3];
                    is3.read(buffer_path);
                    is3.close();

                    // Convert the buffer into a string.
                    String text_product = new String(buffer_product);

                    String lines_product[] = text_product.split("\\r?\\n");

                    String text_rack = new String(buffer_rack);

                    String lines_rack[] = text_rack.split("\\r?\\n");

                    String text_path = new String(buffer_path);

                    String lines_path[] = text_path.split("\\r?\\n");

                    /*String[][] array_lines_path = new String[lines_path.length][];

                    for(i=0;i<lines_path.length;i++)
                        array_lines_path[i] = lines_path[i].split(" ");*/

                    int[] rackList = new int[listArray.size()];
                    for(i=0;i<rackList.length;i++)
                        rackList[i] = -1;

                    int k = 0;

                    for (i = 0; i < listArray.size(); i++) {
                        product_name = listArray.get(i);
                        Log.d(MainActivity.TAG,product_name);
                        for (j = 0; j < 3620; j++) {
                            if(product_name.equals(lines_product[j])){
                                Log.d(MainActivity.TAG,"Rack No: "+lines_rack[j]);
                                rackList[k] = Integer.parseInt(lines_rack[j]);
                                k++;
                                break;
                            }
                        }
                    }
                    Integer[] rackorder = new Integer[k];
                    int t = 0;
                    for(i=0;i<rackList.length;i++) {
                        if (rackList[i] != -1) {
                            rackorder[t] = rackList[i];
                            t++;
                        }
                        Log.d(MainActivity.TAG,""+rackList[i]);
                    }
                    for(i=0;i<rackorder.length;i++)
                        Log.d(MainActivity.TAG,"rackorder:"+rackorder[i]);

                    //Create set from array elements
                    LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>( Arrays.asList(rackorder) );

                    //Get back the array without duplicates
                    Integer[] uniquerackorder = linkedHashSet.toArray(new Integer[] {});

                    int temp;
                    for (i = 0; i < uniquerackorder.length; i++)
                    {
                        for (j = i + 1; j < uniquerackorder.length; j++) {
                            if (uniquerackorder[i] > uniquerackorder[j])
                            {
                                temp = uniquerackorder[i];
                                uniquerackorder[i] = uniquerackorder[j];
                                uniquerackorder[j] = temp;
                            }
                        }
                    }
                    for(i=0;i<rackorder.length;i++)
                        Log.d(MainActivity.TAG,"RackOrder:"+rackorder[i]);
                    for(i=0;i<uniquerackorder.length;i++)
                        Log.d(MainActivity.TAG,"UniqueRackOrder:"+uniquerackorder[i]);
                    int[][] newDistanceMatrix = new int[uniquerackorder.length][uniquerackorder.length];
                    for(i=0;i<uniquerackorder.length;i++)
                    {
                        for(j=0;j<uniquerackorder.length;j++)
                        {
                            newDistanceMatrix[i][j] = distanceMatrix[uniquerackorder[i]][uniquerackorder[j]];
                            Log.d(MainActivity.TAG,""+newDistanceMatrix[i][j]);
                        }
                    }

                    Log.d(MainActivity.TAG,"Lines Path:\n");
                    int rackflag = 0;
                    for(i=0;i<(lines_path.length);i++){
                        String[] tokens = lines_path[i].split(" ");
                        Integer[] array_path = new Integer[tokens.length];
                        int y = 0;
                        for (String token : tokens){
                            array_path[y++] = Integer.parseInt(token);
                        }
                        if(compareArrays(array_path, uniquerackorder))
                        {
                            for(i=0;i<((array_path.length)-1);i++)
                            {
                                var_rack_order += "" + array_path[i] + "->";
                                Log.d(MainActivity.TAG,"Optmized Path:"+array_path[i]);
                            }
                            var_rack_order += array_path[i];
                            rackflag = 1;
                            break;
                        }
                    }
                    if(rackflag == 1)
                    {
                        rackflag = 0;
                        String output = "";
                        String finalOutput = "";
                        Integer[] sortedRackOrder = new Integer[rackorder.length];
                        String[] sortedListArray = new String[listArray.size()];
                        for(i=0;i<rackorder.length;i++)
                        {
                            sortedRackOrder[i] = rackorder[i];
                            sortedListArray[i] = listArray.get(i);
                        }
                        int temp1;
                        String temp2;
                        for (i = 0; i < sortedRackOrder.length; i++)
                        {
                            for (j = i + 1; j < sortedRackOrder.length; j++) {
                                if (sortedRackOrder[i] > sortedRackOrder[j])
                                {
                                    temp1 = sortedRackOrder[i];
                                    sortedRackOrder[i] = sortedRackOrder[j];
                                    sortedRackOrder[j] = temp1;
                                    temp2 = sortedListArray[i];
                                    sortedListArray[i] = sortedListArray[j];
                                    sortedListArray[j] = temp2;
                                }
                            }
                        }
                        finalOutput += "The Shortest path is : \n" + var_rack_order + "\n----------------------------\n";
                        for(i=0;i<sortedRackOrder.length;i++)
                            output += "" + sortedRackOrder[i] + "->" + sortedListArray[i] + "\n" + "----------------------------" + "\n";
                        finalOutput += output;
                        showCustomDialog(finalOutput);
                    }


                    var_rack_order = "";


                    //Log.d(MainActivity.TAG,""+lines_path[i]);


                    for(i=0;i<rackList.length;i++)
                        rackList[i] = -1;

                } catch (IOException e) {
                    // Should never happen!
                    throw new RuntimeException(e);
                }
            }
        };



        /** Setting the event listener for the add button */
        btn.setOnClickListener(listener);

        /** Setting the event listener for the delete button */
        btnDel.setOnClickListener(listenerDel);

        btnDone.setOnClickListener(listenerDone);

        /** Setting the adapter to the ListView */
        setListAdapter(adapter2);
    }

    public static boolean compareArrays(Integer[] arr1, Integer[] arr2) {
        HashSet<Integer> set1 = new HashSet<Integer>(Arrays.asList(arr1));
        HashSet<Integer> set2 = new HashSet<Integer>(Arrays.asList(arr2));
        return set1.equals(set2);
    }

    private void showCustomDialog(String var_rack_order) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);


        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.my_dialog, viewGroup, false);

        Button buttonOk = (Button)dialogView.findViewById(R.id.buttonOk);

        TextView txtPath = (TextView)dialogView.findViewById(R.id.txtPath);
        txtPath.setText(var_rack_order);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        View.OnClickListener listenerOk = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        };


        buttonOk.setOnClickListener(listenerOk);

    }


    private void fillCountryList() {
        countryList = new ArrayList<>();
        try {
            InputStream is = getAssets().open("array.txt");

            // We guarantee that the available method returns the total
            // size of the asset...  of course, this does mean that a single
            // asset can't be more than 2 gigs.
            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string.
            String text = new String(buffer);

            String lines[] = text.split("\\r?\\n");
            for(i=0;i<3620;i++)
                countryList.add(new CountryItem(lines[i]));
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
    }
}