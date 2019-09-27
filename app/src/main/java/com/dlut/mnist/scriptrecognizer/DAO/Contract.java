package com.dlut.mnist.scriptrecognizer.DAO;

import android.provider.BaseColumns;

public class Contract {
    public static class StudentEntry implements BaseColumns {
        public static String DATABASE_NAME = "Database.db";
        public static int DATABASE_VERSION = 1;
        public static String TABLE_STUDENT_INFO = "Student";
        public static String COLUMN_STUDENT_NAME = "Name";
        public static String COLUMN_STUDENT_STUDENTNUMBER = "StudentNumber";
        public static String TABLE_SUBMIT_INFO = "Submit";
        public static String COLUMN_SUBMIT_ID = "ID";
        public static String COLUMN_SUBMIT_FLAG = "Flag";
        public static String COLUMN_SUBMIT_SCORE = "Score";
    }
}
