package com.dlut.mnist.scriptrecognizer.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {
    private static DbHelper m_dbHelper;
    private static Context m_ctx;
    private static String[] columns = new String[]{
            Contract.StudentEntry._ID,                   //  第0列
            Contract.StudentEntry.COLUMN_STUDENT_NAME,      //  第1列
            Contract.StudentEntry.COLUMN_STUDENT_STUDENTNUMBER   //  第2列
    };
    //  设置上下文
    public static void setContext(Context ctx) {
        m_ctx = ctx;
        if (m_dbHelper == null) {
            m_dbHelper = new DbHelper(m_ctx);
            // m_dbHelper.createTable("testTable3");
        }
    }

    //  按照学生姓名和学号添加至数据库
    public static long insertStudent(Student stu) {
        assert (m_dbHelper != null);
        SQLiteDatabase db = m_dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.StudentEntry.COLUMN_STUDENT_NAME, stu.getName());
        values.put(Contract.StudentEntry.COLUMN_STUDENT_STUDENTNUMBER, stu.getStunumber());
        long insertcount = db.insert(Contract.StudentEntry.TABLE_STUDENT_INFO, null, values);
        db.close();
        return insertcount;
    }

    //  按照姓名删除学生
    public static int deleteStudent(String name) {
        assert (m_dbHelper != null);
        SQLiteDatabase db = m_dbHelper.getWritableDatabase();
        String whereClause = Contract.StudentEntry.COLUMN_STUDENT_NAME + " = ?";
        String[] whereArgs = new String[]{name};
        int deletecount = db.delete(Contract.StudentEntry.TABLE_STUDENT_INFO, whereClause, whereArgs);
        db.close();
        return deletecount;
    }

    //  按照学号删除学生
    public static int deleteStudent(int stunum) {
        assert (m_dbHelper != null);
        SQLiteDatabase db = m_dbHelper.getWritableDatabase();
        String whereClause = Contract.StudentEntry.COLUMN_STUDENT_STUDENTNUMBER + " = ?";
        String[] whereArgs = new String[]{String.valueOf(stunum)};
        int deletecount = db.delete(Contract.StudentEntry.TABLE_STUDENT_INFO, whereClause, whereArgs);
        db.close();
        return deletecount;
    }

    //  按姓名+学号双匹配方式删除学生
    public static int deleteStudent(String name, int stunum) {
        assert (m_dbHelper != null);
        SQLiteDatabase db = m_dbHelper.getWritableDatabase();
        String whereClause =
                Contract.StudentEntry.COLUMN_STUDENT_NAME + " = ?" + " AND " +
                Contract.StudentEntry.COLUMN_STUDENT_STUDENTNUMBER + " = ?";
        String[] whereArgs = new String[]{name, String.valueOf(stunum)};
        int deletecount = db.delete(Contract.StudentEntry.TABLE_STUDENT_INFO, whereClause, whereArgs);
        db.close();
        return deletecount;
    }

    //  根据学号更新姓名
    public static int updateStudent(int stunum, String newname) {
        assert (m_dbHelper != null);
        SQLiteDatabase db = m_dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.StudentEntry.COLUMN_STUDENT_NAME, newname);
        String whereClause = Contract.StudentEntry.COLUMN_STUDENT_STUDENTNUMBER + " = ?";
        String[] whereArgs = new String[]{String.valueOf(stunum)};
        int updatecount = db.update(Contract.StudentEntry.TABLE_STUDENT_INFO, values, whereClause, whereArgs);
        db.close();
        return updatecount;
    }

    //  查询所有条目
    public static List<Student> queryStudent() {
        assert (m_dbHelper != null);
        SQLiteDatabase db = m_dbHelper.getWritableDatabase();
        Cursor cursor = db.query(Contract.StudentEntry.TABLE_STUDENT_INFO,
                columns, null, null, null, null, null);
        ArrayList<Student> stulist = new ArrayList<Student>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String tmpname = cursor.getString(cursor.getColumnIndex(Contract.StudentEntry.COLUMN_STUDENT_NAME));
                int tmpstunumber = cursor.getInt(cursor.getColumnIndex(Contract.StudentEntry.COLUMN_STUDENT_STUDENTNUMBER));
                int tmp_id = cursor.getInt(cursor.getColumnIndex(Contract.StudentEntry._ID));
                Student tmpstu = new Student(tmp_id, tmpname, tmpstunumber);
                stulist.add(tmpstu);
            }
        }
        db.close();
        return stulist;
    }

    //  按照学号查找条目
    public static List<Student> queryStudent(int stunum) {
        assert (m_dbHelper != null);
        SQLiteDatabase db = m_dbHelper.getWritableDatabase();
        String selection = Contract.StudentEntry.COLUMN_STUDENT_STUDENTNUMBER + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(stunum)};
        Cursor cursor = db.query(Contract.StudentEntry.TABLE_STUDENT_INFO,
                columns, selection, selectionArgs, null, null, null);
        ArrayList<Student> stulist = new ArrayList<Student>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String tmpname = cursor.getString(cursor.getColumnIndex(Contract.StudentEntry.COLUMN_STUDENT_NAME));
                int tmpstunumber = cursor.getInt(cursor.getColumnIndex(Contract.StudentEntry.COLUMN_STUDENT_STUDENTNUMBER));
                int tmp_id = cursor.getInt(cursor.getColumnIndex(Contract.StudentEntry._ID));
                Student tmpstu = new Student(tmp_id, tmpname, tmpstunumber);
                stulist.add(tmpstu);
            }
        }
        db.close();
        return stulist;
    }

    //  按照姓名查找条目
    public static List<Student> queryStudent(String name) {
        assert (m_dbHelper != null);
        SQLiteDatabase db = m_dbHelper.getWritableDatabase();
        String selection = Contract.StudentEntry.COLUMN_STUDENT_NAME + " = ?";
        String[] selectionArgs = new String[]{name};
        Cursor cursor = db.query(Contract.StudentEntry.TABLE_STUDENT_INFO,
                columns, selection, selectionArgs, null, null, null);
        ArrayList<Student> stulist = new ArrayList<Student>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String tmpname = cursor.getString(cursor.getColumnIndex(Contract.StudentEntry.COLUMN_STUDENT_NAME));
                int tmpstunumber = cursor.getInt(cursor.getColumnIndex(Contract.StudentEntry.COLUMN_STUDENT_STUDENTNUMBER));
                int tmp_id = cursor.getInt(cursor.getColumnIndex(Contract.StudentEntry._ID));
                Student tmpstu = new Student(tmp_id, tmpname, tmpstunumber);
                stulist.add(tmpstu);
            }
        }
        db.close();
        return stulist;
    }

    //  按照姓名&学号双匹配方式查找条目
    public static List<Student> queryStudent(String name, int stunum) {
        assert (m_dbHelper != null);
        SQLiteDatabase db = m_dbHelper.getWritableDatabase();
        String selection =
                Contract.StudentEntry.COLUMN_STUDENT_NAME + " = ?" + " AND " +
                        Contract.StudentEntry.COLUMN_STUDENT_STUDENTNUMBER + " = ?";
        String[] selectionArgs = new String[]{name, String.valueOf(stunum)};
        Cursor cursor = db.query(Contract.StudentEntry.TABLE_STUDENT_INFO,
                columns, selection, selectionArgs, null, null, null);
        ArrayList<Student> stulist = new ArrayList<Student>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String tmpname = cursor.getString(cursor.getColumnIndex(Contract.StudentEntry.COLUMN_STUDENT_NAME));
                int tmpstunumber = cursor.getInt(cursor.getColumnIndex(Contract.StudentEntry.COLUMN_STUDENT_STUDENTNUMBER));
                int tmp_id = cursor.getInt(cursor.getColumnIndex(Contract.StudentEntry._ID));
                Student tmpstu = new Student(tmp_id, tmpname, tmpstunumber);
                stulist.add(tmpstu);
            }
        }
        db.close();
        return stulist;
    }
}
