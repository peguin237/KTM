package com.example.ktm;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.checkerframework.checker.index.qual.NonNegative;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton mcreatenotesfab;

    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<firebasemodel,NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mcreatenotesfab=findViewById(R.id.createnotefab);


        firebaseFirestore =FirebaseFirestore.getInstance();

        mcreatenotesfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,CreateNote.class));
            }
        });


        Query query= firebaseFirestore.collection("notes").orderBy("title",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<firebasemodel> allnotes = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query,firebasemodel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allnotes){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull firebasemodel firebasemodel){

                ImageView popupbutton = noteViewHolder.itemView.findViewById(R.id.menupopbutton);

                int colorcode=getRandomColor();
                noteViewHolder.mnote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colorcode,null));

                noteViewHolder.notetitle.setText(firebasemodel.getTitle());
                noteViewHolder.notecontent.setText(firebasemodel.getContent());

                String docId=noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){

                        Intent intent = new Intent(view.getContext(),notedetails.class);
                        intent.putExtra("title",firebasemodel.getTitle());
                        intent.putExtra("content",firebasemodel.getContent());
                        intent.putExtra("noteId",docId);

                        view.getContext().startActivity(intent);
                        //Toast.makeText(getApplicationContext(),"This is Clicked",Toast.LENGTH_SHORT).show();
                    }
                });

                popupbutton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                                Intent intent = new Intent(view.getContext(),editnoteactivity.class);
                                intent.putExtra("title",firebasemodel.getTitle());
                                intent.putExtra("content",firebasemodel.getContent());
                                intent.putExtra("noteId",docId);
                                view.getContext().startActivity(intent);
                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                                DocumentReference documentReference = firebaseFirestore.collection("notes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(view.getContext(),"This note is deleted",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(),"Failed to delete",Toast.LENGTH_SHORT).show();
                                    }
                                });



                                return false;
                            }
                        });

                        popupMenu.show();

                    }
                });


            }
            @NonNull
            @Override
            public  NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noteslayout,parent,false);
                return new NoteViewHolder(view);
            }
        };
        mrecyclerview=findViewById(R.id.recyclerview);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(noteAdapter);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder
    {
        private TextView notetitle;
        private TextView notecontent;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView){
            super(itemView);
            notetitle=itemView.findViewById(R.id.notetitle);
            notecontent=itemView.findViewById(R.id.notecontent);
            mnote=itemView.findViewById(R.id.note);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        noteAdapter.startListening();
    }

//    @Override
//    protected void onStop(){
//        super.onStop();
//        if(noteAdapter!=null)
//        {
//            noteAdapter.stopListening();
//        }
//    }

    private int getRandomColor()
    {
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.green);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);

        Random random = new Random();
        int number = random.nextInt(colorcode.size());
        return colorcode.get(number);
    }
}