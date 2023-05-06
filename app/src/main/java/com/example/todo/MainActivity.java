package com.example.todo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView listView;
    private Button buttonAdd;
    private EditText input;
    private Boolean on_edit = false;
    private int on_edit_pos;
    public String complete = "Выполнено: ";
    SharedPreferences sPref;
    final String SAVED_DATA = "saved_data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        buttonAdd = findViewById(R.id.buttonAdd);
        input = findViewById(R.id.taskAdd);
        input.setSelectAllOnFocus(true);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(view);
            }
        });

        items = new ArrayList<>();
        //itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        itemsAdapter = new ArrayAdapter<>(this, R.layout.row, items);
        listView.setAdapter(itemsAdapter);
        setUplistViewListener();
        loadData();
    }

    private void setUplistViewListener() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = getApplicationContext();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true)
                        .setPositiveButton("Редактировать", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(items.get(position).toString().contains(complete)){
                                    Toast.makeText(MainActivity.this, "Невозможно изменить выполненую задачу", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                input.setText(items.get(position).toString());
                                on_edit_pos = position;
                                on_edit = true;
                            }
                        })
                        .setNegativeButton("Удалить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                items.remove(position);
                                Toast.makeText(MainActivity.this, "Задача удалена", Toast.LENGTH_SHORT).show();
                                saveData();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.setTitle("Что сделать с данной задачей?");
                dialog.show();

                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!items.get(position).contains(complete)) {
                    items.set(position, complete + items.get(position));
                    }
                else{
                    items.set(position, items.get(position).toString().replace(complete, ""));
                }
                saveData();
            }
        });
    }

    private void addItem(View view) {
        String itemText = input.getText().toString();

        if(!on_edit & !(itemText.trim()).equals("") & !itemText.contains(complete)){
            itemsAdapter.add(itemText.trim());
            input.setText("");
        } else if (on_edit & !(itemText.trim()).equals("")) {
            items.set(on_edit_pos, itemText.trim());
            Toast.makeText(MainActivity.this, "Задача обновлена", Toast.LENGTH_SHORT).show();
            on_edit = false;
            input.setText("");
        } else {
            Toast.makeText(getApplicationContext(), "Пустая строка не может являться задачей", Toast.LENGTH_LONG).show();
            input.setText("");
        }
        saveData();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(MainActivity.this, "Редактирование отменено", Toast.LENGTH_SHORT).show();
            on_edit = false;
            input.setText("");

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void saveData(){
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        
        String data = "";
        for (String s: items ) {
            data += s + "~~";
        }
        
        ed.putString(SAVED_DATA, data);
        ed.commit();
        //Toast.makeText(MainActivity.this, "data saved", Toast.LENGTH_SHORT).show();
        itemsAdapter.notifyDataSetChanged();
    }
    public void loadData(){
        sPref = getPreferences(MODE_PRIVATE);
        String savedData = sPref.getString(SAVED_DATA, "");
        savedData = savedData.replace("[", "").replace("]", "");
        String[] list1 = (savedData.split("~~"));
        for (String s : list1) {
            itemsAdapter.add(s);
        }
        itemsAdapter.notifyDataSetChanged();
        //Toast.makeText(MainActivity.this, "data loaded", Toast.LENGTH_SHORT).show();
    }
}