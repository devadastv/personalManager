package com.dtv.personalmanager;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class PersonalManager extends ListActivity {

	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    
	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	DbAdapter mDbAdaptor;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_manager);
        
        initDbAdaptor();
        displayEntries();
        registerForContextMenu(getListView());
    }
    
	private void initDbAdaptor() {
		mDbAdaptor = new DbAdapter(getApplicationContext());
        mDbAdaptor.open();		
	}

	private void displayEntries() {
		Cursor entriesCursor = mDbAdaptor.fetchAllNotes();
		startManagingCursor(entriesCursor);
		// Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{DbAdapter.KEY_DESCRIPTION};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_row, entriesCursor, from, to);
        setListAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, INSERT_ID, 0, R.string.add_entry);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
			case INSERT_ID:
				createEntry();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, DELETE_ID, 0, R.string.delete_entry);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			mDbAdaptor.deleteNote(info.id);
			displayEntries();
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	private void createEntry() {
		Intent intent = new Intent(this, EntryCreate.class);
		startActivityForResult(intent, ACTIVITY_CREATE);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, EntryCreate.class);
        i.putExtra(DbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        displayEntries();
    }
}
