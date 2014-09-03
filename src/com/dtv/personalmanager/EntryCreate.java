package com.dtv.personalmanager;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class EntryCreate extends Activity {

	EditText mDescriptionText;
	EditText mAmountText;
	Spinner mCurrencySpinner;
	DbAdapter mDbAdapter;
	private Long mRowId;
	ToggleButton mTransactionTypeToggleButton;
	Spinner mCategorySpinner;
	private final boolean TRANSACTION_TYPE_DEBIT = true;
	private final boolean TRANSACTION_TYPE_CREDIT = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry_create);
		setTitle(R.string.add_entry);
		
		mDbAdapter = new DbAdapter(this);
		mDbAdapter.open();
		
		mDescriptionText = (EditText) findViewById(R.id.description);
		mAmountText = (EditText) findViewById(R.id.amount);
		mCurrencySpinner = (Spinner) findViewById(R.id.currency);
		mTransactionTypeToggleButton = (ToggleButton) findViewById(R.id.transactionType);
		mCategorySpinner = (Spinner) findViewById(R.id.category);
		
		mDescriptionText.requestFocus();
		
		mRowId = savedInstanceState == null ? null : (Long)savedInstanceState.getSerializable(DbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(DbAdapter.KEY_ROWID) : null;
        }

        if (mRowId != null)
        {
            Cursor cursor = mDbAdapter.fetchNote((long) mRowId);
            displayValues(cursor);
        }
        else
        {
        	updateCategorySpinner(mTransactionTypeToggleButton.isChecked());
        }
        
        configureSaveButton();
        configureCancelButton();
		mTransactionTypeToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		    	updateCategorySpinner(isChecked);
		    }
		});	
	}

	private void configureSaveButton() {
		Button saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveState();
				setResult(RESULT_OK);
                finish();
			}
		});		
	}
	
	private void configureCancelButton() {
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});		
	}


	private void updateCategorySpinner(boolean isChecked) {
		if (isChecked) {
            // The toggle is enabled
        	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EntryCreate.this,
    		        R.array.currencies, android.R.layout.simple_spinner_item);
    		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		mCategorySpinner.setAdapter(adapter);
        } else {
            // The toggle is disabled
        	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EntryCreate.this,
    		        R.array.categories, android.R.layout.simple_spinner_item);
    		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		mCategorySpinner.setAdapter(adapter);
        }
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	saveState();
    	outState.putSerializable(DbAdapter.KEY_ROWID, mRowId);
    }
    
    
    @Override
    protected void onPause() {
    	super.onPause();
    	saveState();
    	mDbAdapter.close();
    }

    
	private void saveState() {
		String description = mDescriptionText.getText().toString();
		
		if (null != description && description.trim().length() != 0)
		{
			if (mRowId != null)
			{
				mDbAdapter.updateNote(mRowId, mDescriptionText.getText().toString(), mAmountText.getText().toString(), 
						mCurrencySpinner.getSelectedItem().toString(), mTransactionTypeToggleButton.isChecked(), 
						mCategorySpinner.getSelectedItem().toString());
			}
			else
			{
				mRowId = mDbAdapter.createNote(mDescriptionText.getText().toString(), mAmountText.getText().toString(), 
						mCurrencySpinner.getSelectedItem().toString(), mTransactionTypeToggleButton.isChecked(), 
						mCategorySpinner.getSelectedItem().toString());
			}		
		}
		
	}

	private void displayValues(Cursor cursor) {
		startManagingCursor(cursor);
		String description = cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.KEY_DESCRIPTION));
		String amount = cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.KEY_AMOUNT));
		String currency = cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.KEY_CURRENCY));
		boolean transactionType = cursor.getInt(cursor.getColumnIndexOrThrow(DbAdapter.KEY_TYPE)) > 0;
		String category = cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.KEY_CATEGORY));
		
		mDescriptionText.setText(description);
		mAmountText.setText(amount);
		
//		ArrayAdapter myAdap = (ArrayAdapter) mCurrencySpinner.getAdapter();
//		int spinnerPosition = ((ArrayAdapter) mCurrencySpinner.getAdapter()).getPosition(currency);
		mCurrencySpinner.setSelection(((ArrayAdapter) mCurrencySpinner.getAdapter()).getPosition(currency));
		mTransactionTypeToggleButton.setChecked(transactionType);
		updateCategorySpinner(transactionType);
		mCategorySpinner.setSelection(((ArrayAdapter) mCategorySpinner.getAdapter()).getPosition(category));
	}
}
