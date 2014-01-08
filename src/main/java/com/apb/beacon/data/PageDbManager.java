package com.apb.beacon.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.apb.beacon.AppConstants;
import com.apb.beacon.model.Page;
import com.apb.beacon.model.PageAction;
import com.apb.beacon.model.PageItem;
import com.apb.beacon.model.PageStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aoe on 1/5/14.
 */
public class PageDbManager {

    private static final String TAG = PageDbManager.class.getSimpleName();

    private static final String TABLE_WIZARD_PAGE = "wizard_page_table";

    private static final String PAGE_ID = "page_id";
    private static final String PAGE_LANGUAGE = "page_language";
    private static final String PAGE_TYPE = "page_type";
    private static final String PAGE_TITLE = "page_title";
    private static final String PAGE_INTRODUCTION = "page_introduction";
    private static final String PAGE_WARNING = "page_warning";
    private static final String PAGE_COMPONENT = "page_component";
    private static final String PAGE_CONTENT = "page_content";

    private static final String CREATE_TABLE_WIZARD_PAGE = "create table " + TABLE_WIZARD_PAGE + " ( "
            + AppConstants.TABLE_PRIMARY_KEY + " integer primary key autoincrement, " + PAGE_ID + " text, " + PAGE_LANGUAGE + " text, "
            + PAGE_TYPE + " text, " + PAGE_TITLE + " text, " + PAGE_INTRODUCTION + " text, " + PAGE_WARNING + " text, "
            + PAGE_COMPONENT + " text, " + PAGE_CONTENT + " text);";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WIZARD_PAGE);
    }

    public static void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WIZARD_PAGE);
    }


    public static long insert(SQLiteDatabase db, Page page) throws SQLException {

        ContentValues cv = new ContentValues();

        cv.put(PAGE_ID, page.getId());
        cv.put(PAGE_LANGUAGE, page.getLang());
        cv.put(PAGE_TYPE, page.getType());
        cv.put(PAGE_TITLE, page.getTitle());
        cv.put(PAGE_INTRODUCTION, page.getIntroduction());
        cv.put(PAGE_WARNING, page.getWarning());
        cv.put(PAGE_COMPONENT, page.getComponent());
        cv.put(PAGE_CONTENT, page.getContent());

        return db.insert(TABLE_WIZARD_PAGE, null, cv);
    }


    public static Page retrieve(SQLiteDatabase db, String pageId, String lang) throws SQLException {
        Page page = null;

        Cursor c = db.query(TABLE_WIZARD_PAGE, null, PAGE_ID + "=? AND " + PAGE_LANGUAGE + "=?", new String[]{pageId, lang}, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String pageType = c.getString(c.getColumnIndex(PAGE_TYPE));
            String pageTitle = c.getString(c.getColumnIndex(PAGE_TITLE));
            String pageIntro = c.getString(c.getColumnIndex(PAGE_INTRODUCTION));
            String pageWarning = c.getString(c.getColumnIndex(PAGE_WARNING));
            String pageComponent = c.getString(c.getColumnIndex(PAGE_COMPONENT));
            String pageContent = c.getString(c.getColumnIndex(PAGE_CONTENT));

            List<PageStatus> statusList = PageStatusDbManager.retrieve(db, pageId, lang);
            List<PageAction> actionList = PageActionDbManager.retrieve(db, pageId, lang);
            List<PageItem> itemList = PageItemDbManager.retrieve(db, pageId, lang);

            page = new Page(pageId, lang, pageType, pageTitle, pageIntro, pageWarning, pageComponent, statusList, actionList, itemList, pageContent);
        }
        c.close();
        return page;
    }

    public static List<Page> retrieve(SQLiteDatabase db, String lang) throws SQLException {
        List<Page> pageList = new ArrayList<Page>();

        Cursor c = db.query(TABLE_WIZARD_PAGE, null, PAGE_LANGUAGE + "=?", new String[]{lang}, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String pageId = c.getString(c.getColumnIndex(PAGE_ID));
                String pageType = c.getString(c.getColumnIndex(PAGE_TYPE));
                String pageTitle = c.getString(c.getColumnIndex(PAGE_TITLE));
                String pageIntro = c.getString(c.getColumnIndex(PAGE_INTRODUCTION));
                String pageWarning = c.getString(c.getColumnIndex(PAGE_WARNING));
                String pageComponent = c.getString(c.getColumnIndex(PAGE_COMPONENT));
                String pageContent = c.getString(c.getColumnIndex(PAGE_CONTENT));

                List<PageStatus> statusList = PageStatusDbManager.retrieve(db, pageId, lang);
                List<PageAction> actionList = PageActionDbManager.retrieve(db, pageId, lang);
                List<PageItem> itemList = PageItemDbManager.retrieve(db, pageId, lang);

                Page page = new Page(pageId, lang, pageType, pageTitle, pageIntro, pageWarning, pageComponent, statusList, actionList, itemList, pageContent);
                pageList.add(page);
                c.moveToNext();
            }
        }
        c.close();
        return pageList;
    }


    public static long update(SQLiteDatabase db, Page page) throws SQLException {

        ContentValues cv = new ContentValues();

        cv.put(PAGE_TYPE, page.getType());
        cv.put(PAGE_TITLE, page.getTitle());
        cv.put(PAGE_INTRODUCTION, page.getIntroduction());
        cv.put(PAGE_WARNING, page.getWarning());
        cv.put(PAGE_COMPONENT, page.getComponent());
        cv.put(PAGE_CONTENT, page.getContent());

        return db.update(TABLE_WIZARD_PAGE, cv, PAGE_ID + "=? AND " + PAGE_LANGUAGE + "=?", new String[]{page.getId(), page.getLang()});
    }


    public static boolean isExist(SQLiteDatabase db, String pageId, String lang) throws SQLException {
        boolean itemExist = false;

        Cursor c = db.query(TABLE_WIZARD_PAGE, null, PAGE_ID + "=? AND " + PAGE_LANGUAGE + "=?", new String[]{pageId, lang}, null, null, null);

        if ((c != null) && (c.getCount() > 0)) {
            itemExist = true;
        }
        c.close();
        return itemExist;
    }


    public static void insertOrUpdate(SQLiteDatabase db, Page page){
        if(isExist(db, page.getId(), page.getLang())){
            update(db, page);
        }
        else{
            insert(db, page);
        }
    }


    public static int delete(SQLiteDatabase db, String pageId, String lang){
        return db.delete(TABLE_WIZARD_PAGE, PAGE_ID + "=? AND " + PAGE_LANGUAGE + "=?", new String[]{pageId, lang});
    }
}
