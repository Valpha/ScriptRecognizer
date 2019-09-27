package com.dlut.mnist.scriptrecognizer.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.dlut.mnist.scriptrecognizer.DAO.Contract.StudentEntry.COLUMN_STUDENT_NAME;
import static com.dlut.mnist.scriptrecognizer.DAO.Contract.StudentEntry.COLUMN_STUDENT_STUDENTNUMBER;
import static com.dlut.mnist.scriptrecognizer.DAO.Contract.StudentEntry.COLUMN_SUBMIT_FLAG;
import static com.dlut.mnist.scriptrecognizer.DAO.Contract.StudentEntry.COLUMN_SUBMIT_ID;
import static com.dlut.mnist.scriptrecognizer.DAO.Contract.StudentEntry.COLUMN_SUBMIT_SCORE;
import static com.dlut.mnist.scriptrecognizer.DAO.Contract.StudentEntry.DATABASE_NAME;
import static com.dlut.mnist.scriptrecognizer.DAO.Contract.StudentEntry.DATABASE_VERSION;
import static com.dlut.mnist.scriptrecognizer.DAO.Contract.StudentEntry.TABLE_STUDENT_INFO;
import static com.dlut.mnist.scriptrecognizer.DAO.Contract.StudentEntry._ID;

public class DbHelper extends SQLiteOpenHelper {

    private static final String CREATE_STUDENT_TABLESQL
            = "create table " + TABLE_STUDENT_INFO
            + " (" + _ID + " integer primary key, "
            + COLUMN_STUDENT_NAME + " text, "
            + COLUMN_STUDENT_STUDENTNUMBER + " integer);";

    private static final String DROP_STUDENT_TABLESQL
            = "drop table " + TABLE_STUDENT_INFO + ";";

    //  构造方法
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_key=ON;");
        }
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STUDENT_TABLESQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_STUDENT_TABLESQL);
    }

    public void createTable(String tableName) {
        String CREATE_TABLE_SUBMIT_WITH_TIME
                = "create table " + tableName
                + " (" + COLUMN_SUBMIT_ID + " integer primary key, "
                + COLUMN_STUDENT_STUDENTNUMBER + " integer, "
                + COLUMN_STUDENT_NAME + " text, "
                + COLUMN_SUBMIT_SCORE + " integer, "
                + COLUMN_SUBMIT_FLAG + " text, "
                + "foreign key (" + COLUMN_STUDENT_STUDENTNUMBER + ") references " + TABLE_STUDENT_INFO +"("+COLUMN_STUDENT_STUDENTNUMBER+")"
                + "foreign key (" + COLUMN_STUDENT_NAME + ") references " + TABLE_STUDENT_INFO +"("+COLUMN_STUDENT_NAME+")"
                + ");";
        getWritableDatabase().execSQL(CREATE_TABLE_SUBMIT_WITH_TIME);

    }
}
