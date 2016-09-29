package dexin.love.band.adapter;

import android.widget.ListAdapter;

public interface PinnedSectionListAdapter extends ListAdapter {
	/** This method shall return 'true' if views of given type has to be pinned. */
	boolean isItemViewTypePinned(int viewType);
}
