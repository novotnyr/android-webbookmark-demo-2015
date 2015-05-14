package sk.upjs.ics.android.webookmarkr;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Browser;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.SimpleCursorAdapter;

public class BookmarkListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, AbsListView.MultiChoiceModeListener {

    private static final int BOOKMARK_LIST_LOADER_ID = 0;

    private SimpleCursorAdapter listViewAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initListAdapter();
        initList();
        setListAdapter(this.listViewAdapter);
        getLoaderManager().initLoader(BOOKMARK_LIST_LOADER_ID, Bundle.EMPTY, this);
    }

    private void initList() {
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(this);
    }

    private void initListAdapter() {
        String[] from = { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL };
        int[] to = { android.R.id.text1, android.R.id.text2 };
        this.listViewAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_activated_2,
                Defaults.NO_CURSOR,
                from, to,
                Defaults.NO_FLAGS);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(getActivity());
        loader.setUri(Browser.BOOKMARKS_URI);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        this.listViewAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.listViewAdapter.swapCursor(Defaults.NO_CURSOR);
    }

    // ----- Fragment UI Logic

    private void deleteSelectedListItems() {
        for (long id : getListView().getCheckedItemIds()) {
            getActivity().getContentResolver().delete(Browser.BOOKMARKS_URI, BaseColumns._ID + " = " + id, Defaults.NO_SELECTION_ARGS);
            getLoaderManager().restartLoader(BOOKMARK_LIST_LOADER_ID, Bundle.EMPTY, this);
        }
    }

    // ----- Contextual Action Bar callbacks

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        int checkedCount = getListView().getCheckedItemCount();
        String checkedCountMessage = getResources().getQuantityString(R.plurals.selected_bookmarks, checkedCount, checkedCount);
        mode.setSubtitle(checkedCountMessage);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.bookmarks_cab, menu);
        mode.setTitle("Select Items");
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch(item.getItemId()) {
            case R.id.listViewDeleteSelectedAction:
                deleteSelectedListItems();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // do nothing
    }
}
