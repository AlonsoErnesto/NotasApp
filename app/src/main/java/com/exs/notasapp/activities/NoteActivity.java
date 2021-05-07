package com.exs.notasapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.exs.notasapp.R;
import com.exs.notasapp.adapters.NoteAdapter;
import com.exs.notasapp.models.Board;
import com.exs.notasapp.models.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

public class NoteActivity extends AppCompatActivity implements RealmChangeListener<Board>  {

    private ListView listView;
    private FloatingActionButton fab;

    private NoteAdapter adapter;
    private RealmList<Note> notes;
    private Realm realm;

    private int boardId;
    private Board board;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        realm = Realm.getDefaultInstance();
        if(getIntent().getExtras() != null ){
            boardId= getIntent().getExtras().getInt("id");
        }

        board = realm.where(Board.class).equalTo("id",boardId).findFirst() ;
        board.addChangeListener(this);
        notes = board.getNotes();

        this.setTitle(board.getTitle());

        fab = (FloatingActionButton)findViewById(R.id.fabAddNote);
        listView = (ListView) findViewById(R.id.listViewNote);
        adapter = new NoteAdapter(this, notes, R.layout.activity_list_view_board_item);
        listView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForCreatingNote("Agregar nueva Nota  ","Esctriba una nota "+ board.getTitle());
            }

        });
        registerForContextMenu(listView);
    }
    //    crud note
    private void createNewNote(String note ){

        realm.beginTransaction();
        Note _note = new Note(note);
        realm.copyToRealm(_note);
        board.getNotes().add(_note);
        realm.commitTransaction();


    }

    private void editNote(String newNoteDescription, Note note){
        realm.beginTransaction();
        note.setDescription(newNoteDescription);
        realm.copyToRealmOrUpdate(note);
        realm.commitTransaction();
    }


    private void deleteNote(Note note){
        realm.beginTransaction();
        note.deleteFromRealm();
        realm.commitTransaction();
    }
    private void deleteAll(){
        realm.beginTransaction();
        board.getNotes().deleteAllFromRealm();
        realm.commitTransaction();
    }


    private void showAlertForCreatingNote(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        if(title != null) {
            builder.setTitle(title);
        };
        if(message != null){
            builder.setMessage(message);
        }

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_note,null);
        builder.setView(viewInflated);

        final EditText input = (EditText)viewInflated.findViewById(R.id.editTextNewNote);


        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                String note = input.getText().toString().trim();
                if(note.length() > 0)
                    createNewNote(note);
                else
                    Toast.makeText(getApplicationContext(),"La nota no puede estar vacio",Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog =  builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_note_ativity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_notes:
            deleteAll();
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu_note_activity,menu);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){
            case R.id.delete_note:
                deleteNote(notes.get(info.position));
                return true;
            case R.id.edit_note:
                showAlertForEditingNotes("Editar nota","cambiar el nombre de la hoja", notes.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }


    }

    @Override
    public void onChange(Board board) {
        adapter.notifyDataSetChanged();
    }

    private void showAlertForEditingNotes(String title, String message, final Note note){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        if(title != null) {
            builder.setTitle(title);
        };
        if(message != null){
            builder.setMessage(message);
        }

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_note,null);
        builder.setView(viewInflated);

        final EditText input = (EditText)viewInflated.findViewById(R.id.editTextNewBoard);
        input.setText(note.getDescription());

        builder.setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String boardName = input.getText().toString().trim();

                if(boardName.length() == 0 ){
                    Toast.makeText(getApplicationContext(),"El titulo a editar es requerido",Toast.LENGTH_LONG).show();
                }else if(boardName.equals(note.getDescription())){
                    Toast.makeText(getApplicationContext(),"El titulo ya existe",Toast.LENGTH_LONG).show();
                }else{
                    editNote(boardName,note);
                }
//
//                if(boardName.length() > 0)
//                    createNewBoard(boardName);
//                else
//                    Toast.makeText(getApplicationContext(),"Este nombre requiere crear una nueva hoja",Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog =  builder.create();
        dialog.show();
    }

}














