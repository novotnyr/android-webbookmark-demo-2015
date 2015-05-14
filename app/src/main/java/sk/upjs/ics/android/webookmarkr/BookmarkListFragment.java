package sk.upjs.ics.android.webookmarkr;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Browser;
import android.view.View;
import android.widget.SimpleCursorAdapter;

public class BookmarkListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOKMARK_LIST_LOADER = 0;

    private SimpleCursorAdapter listViewAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initListAdapter();
        setListAdapter(this.listViewAdapter);
        getLoaderManager().initLoader(BOOKMARK_LIST_LOADER, Bundle.EMPTY, this);
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
}
