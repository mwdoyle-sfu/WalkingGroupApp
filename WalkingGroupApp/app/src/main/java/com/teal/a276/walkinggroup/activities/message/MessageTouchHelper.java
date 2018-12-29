package com.teal.a276.walkinggroup.activities.message;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

/**
 * Listener that allows for messages in a recycle view to be swiped to mark them as read.
 * General idea for the ItemTouchHelper from here: https://medium.com/@kitek/recyclerview-swipe-to-delete-easier-than-you-thought-cff67ff5e5f6
 */
abstract class MessageTouchHelper extends ItemTouchHelper.SimpleCallback {

    MessageTouchHelper(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;
        if ((dX == 0.0 && !isCurrentlyActive)) {
            clearReadIcon(canvas, itemView, dX);
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        ColorDrawable background = new ColorDrawable(Color.parseColor("#64DD17"));
        background.setBounds(itemView.getLeft() + (int)dX, itemView.getTop(),
                itemView.getLeft(), itemView.getBottom());
        background.draw(canvas);
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void clearReadIcon(Canvas canvas, View view, float xOffset) {
        Paint clear = new Paint();
        clear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        clear.setColor(Color.TRANSPARENT);
        canvas.drawRect(view.getLeft() + xOffset, (float)view.getTop(),
                (float)view.getLeft(), (float)view.getBottom(), clear);
    }
}
