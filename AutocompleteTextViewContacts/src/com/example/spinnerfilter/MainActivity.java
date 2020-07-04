package com.example.spinnerfilter;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.database.*;
import android.os.*;
import android.provider.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.core.app.*;
import androidx.core.content.*;

public class MainActivity extends Activity {

    private ContentResolver cr;
    private static final int READ_CONTACT_PERMISSION_CODE = 1;
    private static final int WRITE_CONTACT_PERMISSION_CODE = 2;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        cr = getContentResolver ( );
        AutoCompleteTextView emailText = findViewById ( R.id.emailAddress );
        String[] fromCols = {
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Email.DATA,
        };

        if ( ContextCompat.checkSelfPermission ( this, Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission ( this, Manifest.permission.WRITE_CONTACTS ) != PackageManager.PERMISSION_GRANTED ) {
            //If the permission is not granted
            ActivityCompat.requestPermissions ( MainActivity.this, new String[] { Manifest.permission.READ_CONTACTS },
                    READ_CONTACT_PERMISSION_CODE );
            ActivityCompat.requestPermissions ( MainActivity.this, new String[] { Manifest.permission.WRITE_CONTACTS },
                    WRITE_CONTACT_PERMISSION_CODE );
        } else {
            //If the permission is granted
            int[] toViewIds = { R.id.list_name, R.id.list_email };
            SimpleCursorAdapter emailAdapter = new SimpleCursorAdapter ( this, R.layout.email_and_name, getNamesAndEmails ( null ),
                    fromCols, toViewIds, 0 );

            // Important 1: You have to provide a way of making the chosen choice look presentable.
            // emailAdapter.setStringConversionColumn(1); // 1=DISPLAY_NAME, 2=Email
            emailAdapter.setCursorToStringConverter ( new SimpleCursorAdapter.CursorToStringConverter ( ) {
                @Override
                public CharSequence convertToString ( Cursor cursor ) {
                    return String.format ( "%s <%s>", cursor.getString ( 1 ).trim ( ), cursor.getString ( 2 ).trim ( ) );
                }
            } );

            // Important 2: You have to provide a query containing the values on demand
            emailAdapter.setFilterQueryProvider ( new FilterQueryProvider ( ) {

                public Cursor runQuery ( CharSequence constraint ) {
                    String partialItemName = null;
                    if ( constraint != null ) {
                        partialItemName = constraint.toString ( );
                    }
                    return getNamesAndEmails ( partialItemName );
                }
            } );

            emailText.setAdapter ( emailAdapter );
        }
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );
    }

    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ( ).inflate ( R.menu.main, menu );
        return true;
    }

    final static String[] PROJECTION = new String[] {
            ContactsContract.RawContacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Email.DATA,
    };

    /**
     * Get the contacts that have email addresses matching "partialName".
     *
     * @author Modified from code obtained from
     * http://stackoverflow.com/questions/5205999/android-get-a-cursor-only-with-contacts-that-have-an-email-listed-android-2-0
     */
    Cursor getNamesAndEmails ( String partialName ) {
        // Look for partialName either in display name (person name) or in email
        final String filter =
                ContactsContract.Contacts.DISPLAY_NAME + " LIKE '%" + partialName + "%'" +
                        " OR " +
                        ContactsContract.CommonDataKinds.Email.DATA + " LIKE '%" + partialName + "%'";
        // If display name contains "@" (maybe it's null so Contacts provides email here),
        // order by email, else order by display name.
        final String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        // Now make a Cursor containing the contacts that now match partialName as per "filter".
        return cr.query ( ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order );
    }

    // TODO: 2020/07/02 App permissions needs to be added and requested 
}
