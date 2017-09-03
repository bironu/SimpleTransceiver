package com.example.bironu.simpletransceiver.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListAdapter;

/**
 * {@link DialogFragment} like {@link AlertDialog.Builder}
 */
public class AlertDialogFragment extends DialogFragment {

    public interface OnItemClickListener {
        void onItemClick(AlertDialogFragment dialog, int which);
    }

    public interface OnMultiItemClickListener {
        void onItemClick(AlertDialogFragment dialog, int which, boolean isChecked);
    }

    public interface OnClickListener {
        void onClickPositive(AlertDialogFragment dialog);

        void onClickNegative(AlertDialogFragment dialog);

        void onClickNeutral(AlertDialogFragment dialog);
    }

    public interface OnCancelListener {
        void onCancel(AlertDialogFragment dialog);
    }

    public interface OnDismissListener {
        void onDismiss(AlertDialogFragment dialog);
    }

    public interface OnKeyListener {
        boolean onKey(AlertDialogFragment dialog, int keyCode, KeyEvent event);
    }

    public interface ViewDelegate {
        View getView(AlertDialogFragment dialog);
    }

    public interface ListAdapterDelegate {
        ListAdapter getListAdapter(AlertDialogFragment dialog);
    }


    private static final String ARG_THEME = "theme";
    private static final String ARG_ICON = "icon";
    private static final String ARG_INVERSE_BACKGROUND = "inverseBackground";
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";

    private static final String ARG_ITEMS = "items";
    private static final String ARG_ITEMS_LISTENER = "itemsListener";

    private static final String ARG_ADAPTER = "adapter";
    private static final String ARG_ADAPTER_LISTENER = "adapterListener";

    private static final String ARG_CHECKED_ITEMS = "checkedItems";
    private static final String ARG_MULTI_CHOICE_ITEMS = "multiChoiceItems";
    private static final String ARG_MULTI_CHOICE_LISTENER = "multiChoiceListener";

    private static final String ARG_CHECKED_ITEM = "checkedItem";
    private static final String ARG_SINGLE_CHOICE_ITEMS = "singleChoiceItems";
    private static final String ARG_SINGLE_CHOICE_ADAPTER = "singleChoiceAdapter";
    private static final String ARG_SINGLE_CHOICE_LISTENER = "singleChoiceListener";

    private static final String ARG_NEGATIVE_BUTTON = "negative";
    private static final String ARG_NEGATIVE_BUTTON_LISTENER = "negativeListener";

    private static final String ARG_NEUTRAL_BUTTON = "neutral";
    private static final String ARG_NEUTRAL_BUTTON_LISTENER = "neutralListener";

    private static final String ARG_POSITIVE_BUTTON = "positive";
    private static final String ARG_POSITIVE_BUTTON_LISTENER = "positiveListener";

    private static final String ARG_CANCEL_LISTENER = "cancelListener";
    private static final String ARG_DISMISS_LISTENER = "dismissListener";
    private static final String ARG_KEY_LISTENER = "keyListener";

    private static final String ARG_CUSTOM_VIEW = "customView";


    private static final String TAG_ACTIVITY = "activity";
    private static final String TAG_FRAGMENT = "fragment:";

    private static final int VALUE_NULL = 0;
    private static final int VALUE_TRUE = 1;
    private static final int VALUE_FALSE = 2;

    public static class Builder {

        private final Context mContext;
        private final Bundle mArguments = new Bundle();
        private int mCancelable = VALUE_NULL;

        public Builder(Context context) {
            this(context, VALUE_NULL);
        }

        public Builder(Context context, int theme) {
            mContext = context.getApplicationContext();
            mArguments.putInt(ARG_THEME, theme);
        }

        public AlertDialogFragment create() {
            AlertDialogFragment f = new AlertDialogFragment();
            f.setArguments(mArguments);
            if (mCancelable != VALUE_NULL)
                f.setCancelable(mCancelable == VALUE_TRUE);
            return f;
        }

        public void show(FragmentManager manager, String tag) {
            create().show(manager, tag);
        }

        public void show(FragmentTransaction transaction, String tag) {
            create().show(transaction, tag);
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable ? VALUE_TRUE : VALUE_FALSE;
            return this;
        }

        public Builder setIcon(int iconId) {
            mArguments.putInt(ARG_ICON, iconId);
            return this;
        }

        public Builder setInverseBackgroundForced(boolean useInverseBackground) {
            mArguments.putInt(ARG_INVERSE_BACKGROUND, useInverseBackground ? VALUE_TRUE : VALUE_FALSE);
            return this;
        }


        //
        // Title
        //

        public Builder setTitle(CharSequence title) {
            mArguments.putCharSequence(ARG_TITLE, title);
            return this;
        }

        public Builder setTitle(int resId) {
            mArguments.putCharSequence(ARG_TITLE, mContext.getText(resId));
            return this;
        }


        //
        // Message
        //

        public Builder setMessage(CharSequence message) {
            mArguments.putCharSequence(ARG_MESSAGE, message);
            return this;
        }

        public Builder setMessage(int resId) {
            return setMessage(mContext.getText(resId));
        }


        //
        // CustomView
        //

        public <T extends Fragment & ViewDelegate> Builder setView(T delegate) {
            putArgument(ARG_CUSTOM_VIEW, delegate);
            return this;
        }

        public <T extends Activity & ViewDelegate> Builder setView(T delegate) {
            putArgument(ARG_CUSTOM_VIEW, delegate);
            return this;
        }


        //
        // List
        //

        public <T extends Fragment & OnItemClickListener> Builder setItems(CharSequence[] items, T listener) {
            mArguments.putCharSequenceArray(ARG_ITEMS, items);
            putArgument(ARG_ITEMS_LISTENER, listener);
            return this;
        }

        public <T extends Fragment & OnItemClickListener> Builder setItems(int itemResId, T listener) {
            return setItems(mContext.getResources().getTextArray(itemResId), listener);
        }

        public <T extends Activity & OnItemClickListener> Builder setItems(CharSequence[] items, T listener) {
            mArguments.putCharSequenceArray(ARG_ITEMS, items);
            putArgument(ARG_ITEMS_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnItemClickListener> Builder setItems(int itemResId, T listener) {
            return setItems(mContext.getResources().getTextArray(itemResId), listener);
        }


        //
        // Adapter
        //

        public <TAdapter extends Fragment & ListAdapterDelegate,
                TListener extends Fragment & OnItemClickListener> Builder setAdapter(
                TAdapter adapter, TListener listener) {
            putArgument(ARG_ADAPTER, adapter);
            putArgument(ARG_ADAPTER_LISTENER, listener);
            return this;
        }

        public <TAdapter extends Activity & ListAdapterDelegate,
                TListener extends Activity & OnItemClickListener> Builder setAdapter(
                TAdapter adapter, TListener listener) {
            putArgument(ARG_ADAPTER, adapter);
            putArgument(ARG_ADAPTER_LISTENER, listener);
            return this;
        }

        public <TAdapter extends Fragment & ListAdapterDelegate,
                TListener extends Activity & OnItemClickListener> Builder setAdapter(
                TAdapter adapter, TListener listener) {
            putArgument(ARG_ADAPTER, adapter);
            putArgument(ARG_ADAPTER_LISTENER, listener);
            return this;
        }

        public <TAdapter extends Activity & ListAdapterDelegate,
                TListener extends Fragment & OnItemClickListener> Builder setAdapter(
                TAdapter adapter, TListener listener) {
            putArgument(ARG_ADAPTER, adapter);
            putArgument(ARG_ADAPTER_LISTENER, listener);
            return this;
        }


        //
        // MultiChoiceItems
        //

        public <T extends Fragment & OnMultiItemClickListener> Builder setMultiChoiceItems(
                CharSequence[] items, boolean[] checkedItems, T listener) {
            mArguments.putCharSequenceArray(ARG_MULTI_CHOICE_ITEMS, items);
            mArguments.putBooleanArray(ARG_CHECKED_ITEMS, checkedItems);
            putArgument(ARG_MULTI_CHOICE_LISTENER, listener);
            return this;
        }

        public <T extends Fragment & OnMultiItemClickListener> Builder setMultiChoiceItems(
                int itemResId, boolean[] checkedItems, T listener) {
            return setMultiChoiceItems(mContext.getResources().getTextArray(itemResId), checkedItems, listener);
        }

        public <T extends Activity & OnMultiItemClickListener> Builder setMultiChoiceItems(
                CharSequence[] items, boolean[] checkedItems, T listener) {
            mArguments.putCharSequenceArray(ARG_MULTI_CHOICE_ITEMS, items);
            mArguments.putBooleanArray(ARG_CHECKED_ITEMS, checkedItems);
            putArgument(ARG_MULTI_CHOICE_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnMultiItemClickListener> Builder setMultiChoiceItems(
                int itemResId, boolean[] checkedItems, T listener) {
            return setMultiChoiceItems(mContext.getResources().getTextArray(itemResId), checkedItems, listener);
        }


        //
        // SingleChoiceItems
        //

        public <T extends Fragment & OnItemClickListener> Builder setSingleChoiceItems(
                CharSequence[] items, int checkedItem, T listener) {
            mArguments.putCharSequenceArray(ARG_SINGLE_CHOICE_ITEMS, items);
            mArguments.putInt(ARG_CHECKED_ITEM, checkedItem);
            putArgument(ARG_SINGLE_CHOICE_LISTENER, listener);
            return this;
        }

        public <T extends Fragment & OnItemClickListener> Builder setSingleChoiceItems(
                int itemResId, int checkedItem, T listener) {
            return setSingleChoiceItems(mContext.getResources().getTextArray(itemResId), checkedItem, listener);
        }

        public <T extends Activity & OnItemClickListener> Builder setSingleChoiceItems(
                CharSequence[] items, int checkedItem, T listener) {
            mArguments.putCharSequenceArray(ARG_SINGLE_CHOICE_ITEMS, items);
            mArguments.putInt(ARG_CHECKED_ITEM, checkedItem);
            putArgument(ARG_SINGLE_CHOICE_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnItemClickListener> Builder setSingleChoiceItems(
                int itemResId, int checkedItem, T listener) {
            return setSingleChoiceItems(mContext.getResources().getTextArray(itemResId), checkedItem, listener);
        }

        public <TAdapter extends Fragment & ListAdapterDelegate,
                TListener extends Fragment & OnItemClickListener> Builder setSingleChoiceItems(
                TAdapter adapter, int checkedItem, TListener listener) {
            mArguments.putInt(ARG_CHECKED_ITEM, checkedItem);
            putArgument(ARG_SINGLE_CHOICE_ADAPTER, adapter);
            putArgument(ARG_SINGLE_CHOICE_LISTENER, listener);
            return this;
        }

        public <TAdapter extends Activity & ListAdapterDelegate,
                TListener extends Activity & OnItemClickListener> Builder setSingleChoiceItems(
                TAdapter adapter, int checkedItem, TListener listener) {
            mArguments.putInt(ARG_CHECKED_ITEM, checkedItem);
            putArgument(ARG_SINGLE_CHOICE_ADAPTER, adapter);
            putArgument(ARG_SINGLE_CHOICE_LISTENER, listener);
            return this;
        }

        public <TAdapter extends Fragment & ListAdapterDelegate,
                TListener extends Activity & OnItemClickListener> Builder setSingleChoiceItems(
                TAdapter adapter, int checkedItem, TListener listener) {
            mArguments.putInt(ARG_CHECKED_ITEM, checkedItem);
            putArgument(ARG_SINGLE_CHOICE_ADAPTER, adapter);
            putArgument(ARG_SINGLE_CHOICE_LISTENER, listener);
            return this;
        }

        public <TAdapter extends Activity & ListAdapterDelegate,
                TListener extends Fragment & OnItemClickListener> Builder setSingleChoiceItems(
                TAdapter adapter, int checkedItem, TListener listener) {
            mArguments.putInt(ARG_CHECKED_ITEM, checkedItem);
            putArgument(ARG_SINGLE_CHOICE_ADAPTER, adapter);
            putArgument(ARG_SINGLE_CHOICE_LISTENER, listener);
            return this;
        }


        //
        // NegativeButton
        //

        public <T extends Fragment & OnClickListener> Builder setNegativeButton(CharSequence text, T listener) {
            mArguments.putCharSequence(ARG_NEGATIVE_BUTTON, text);
            putArgument(ARG_NEGATIVE_BUTTON_LISTENER, listener);
            return this;
        }

        public <T extends Fragment & OnClickListener> Builder setNegativeButton(int resId, T listener) {
            return setNegativeButton(mContext.getText(resId), listener);
        }

        public <T extends Activity & OnClickListener> Builder setNegativeButton(CharSequence text, T listener) {
            mArguments.putCharSequence(ARG_NEGATIVE_BUTTON, text);
            putArgument(ARG_NEGATIVE_BUTTON_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnClickListener> Builder setNegativeButton(int resId, T listener) {
            return setNegativeButton(mContext.getText(resId), listener);
        }

        public Builder setNegativeButton(CharSequence text) {
            mArguments.putCharSequence(ARG_NEGATIVE_BUTTON, text);
            return this;
        }

        public Builder setNegativeButton(int resId) {
            return setNegativeButton(mContext.getText(resId));
        }


        //
        // NeutralButton
        //

        public <T extends Fragment & OnClickListener> Builder setNeutralButton(CharSequence text, T listener) {
            mArguments.putCharSequence(ARG_NEUTRAL_BUTTON, text);
            putArgument(ARG_NEUTRAL_BUTTON_LISTENER, listener);
            return this;
        }

        public <T extends Fragment & OnClickListener> Builder setNeutralButton(int resId, T listener) {
            return setNeutralButton(mContext.getText(resId), listener);
        }

        public <T extends Activity & OnClickListener> Builder setNeutralButton(CharSequence text, T listener) {
            mArguments.putCharSequence(ARG_NEUTRAL_BUTTON, text);
            putArgument(ARG_NEUTRAL_BUTTON_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnClickListener> Builder setNeutralButton(int resId, T listener) {
            return setNeutralButton(mContext.getText(resId), listener);
        }

        public Builder setNeutralButton(CharSequence text) {
            mArguments.putCharSequence(ARG_NEUTRAL_BUTTON, text);
            return this;
        }

        public Builder setNeutralButton(int resId) {
            return setNeutralButton(mContext.getText(resId));
        }


        //
        // PositiveButton
        //

        public <T extends Fragment & OnClickListener> Builder setPositiveButton(CharSequence text, T listener) {
            mArguments.putCharSequence(ARG_POSITIVE_BUTTON, text);
            putArgument(ARG_POSITIVE_BUTTON_LISTENER, listener);
            return this;
        }

        public <T extends Fragment & OnClickListener> Builder setPositiveButton(int resId, T listener) {
            return setPositiveButton(mContext.getText(resId), listener);
        }

        public <T extends Activity & OnClickListener> Builder setPositiveButton(CharSequence text, T listener) {
            mArguments.putCharSequence(ARG_POSITIVE_BUTTON, text);
            putArgument(ARG_POSITIVE_BUTTON_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnClickListener> Builder setPositiveButton(int resId, T listener) {
            return setPositiveButton(mContext.getText(resId), listener);
        }

        public Builder setPositiveButton(CharSequence text) {
            mArguments.putCharSequence(ARG_POSITIVE_BUTTON, text);
            return this;
        }

        public Builder setPositiveButton(int resId) {
            return setPositiveButton(mContext.getText(resId));
        }


        //
        // CancelListener
        //

        public <T extends Fragment & OnCancelListener> Builder setOnCancelListener(T listener) {
            putArgument(ARG_CANCEL_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnCancelListener> Builder setOnCancelListener(T listener) {
            putArgument(ARG_CANCEL_LISTENER, listener);
            return this;
        }


        //
        // DismissListener
        //

        public <T extends Fragment & OnDismissListener> Builder setOnDismissListener(T listener) {
            putArgument(ARG_DISMISS_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnDismissListener> Builder setOnDismissListener(T listener) {
            putArgument(ARG_DISMISS_LISTENER, listener);
            return this;
        }


        //
        // KeyListener
        //

        public <T extends Fragment & OnKeyListener> Builder setOnKeyListener(T listener) {
            putArgument(ARG_KEY_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnKeyListener> Builder setOnKeyListener(T listener) {
            putArgument(ARG_KEY_LISTENER, listener);
            return this;
        }


        //
        // Helper
        //

        private void putArgument(String key, Fragment fragment) {
            if (fragment != null && fragment.getTag() != null)
                mArguments.putString(key, TAG_FRAGMENT + fragment.getTag());
        }

        private void putArgument(String key, Activity activity) {
            if (activity != null)
                mArguments.putString(key, TAG_ACTIVITY);
        }

    }


    public AlertDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        final int theme = args.getInt(ARG_THEME);

        AlertDialog.Builder builder;
        if (theme == VALUE_NULL || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            builder = new AlertDialog.Builder(getActivity());
        } else {
            builder = newDialogBuilder(theme);
        }

        final CharSequence title = args.getCharSequence(ARG_TITLE);
        if (title != null)
            builder.setTitle(title);

        final CharSequence message = args.getCharSequence(ARG_MESSAGE);
        if (message != null)
            builder.setMessage(message);

        final int iconId = args.getInt(ARG_ICON, VALUE_NULL);
        if (iconId != VALUE_NULL)
            builder.setIcon(iconId);

        final int useInverseBackground = args.getInt(ARG_INVERSE_BACKGROUND);
        if (useInverseBackground != VALUE_NULL)
            builder.setInverseBackgroundForced(useInverseBackground == VALUE_TRUE);

        // View
        setCustomView(builder);

        // List
        setItems(builder);
        setAdapter(builder);
        setMultiChoiceItems(builder);
        setSingleChoiceItems(builder);

        // Buttons
        setPositiveButton(builder);
        setNegativeButton(builder);
        setNeutralButton(builder);

        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final OnKeyListener listener = findListenerByTag(OnKeyListener.class, ARG_KEY_LISTENER);
        if (listener != null) {
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return listener.onKey(AlertDialogFragment.this, keyCode, event);
                }
            });
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        final OnCancelListener listener = findListenerByTag(
                OnCancelListener.class, ARG_CANCEL_LISTENER);
        if (listener != null)
            listener.onCancel(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        final OnDismissListener listener = findListenerByTag(
                OnDismissListener.class, ARG_DISMISS_LISTENER);
        if (listener != null)
            listener.onDismiss(this);
    }

    private void setCustomView(AlertDialog.Builder builder) {
        final ViewDelegate delegate = findListenerByTag(
                ViewDelegate.class, ARG_CUSTOM_VIEW);
        if (delegate == null)
            return;
        builder.setView(delegate.getView(this));
    }

    private void setAdapter(AlertDialog.Builder builder) {
        final ListAdapterDelegate delegate = findListenerByTag(ListAdapterDelegate.class, ARG_ADAPTER);
        if (delegate == null)
            return;

        builder.setAdapter(delegate.getListAdapter(this), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final OnItemClickListener listener = findListenerByTag(
                        OnItemClickListener.class, ARG_ADAPTER_LISTENER);
                listener.onItemClick(AlertDialogFragment.this, which);
            }
        });
    }

    private void setSingleChoiceItems(AlertDialog.Builder builder) {
        final Bundle args = getArguments();
        final CharSequence[] items = args.getCharSequenceArray(ARG_SINGLE_CHOICE_ITEMS);
        final int checkedItem = args.getInt(ARG_CHECKED_ITEM);
        if (items != null) {
            builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final OnItemClickListener listener = findListenerByTag(
                            OnItemClickListener.class, ARG_SINGLE_CHOICE_LISTENER);
                    if (listener != null)
                        listener.onItemClick(AlertDialogFragment.this, which);
                }
            });
            return;
        }

        final ListAdapterDelegate delegate = findListenerByTag(
                ListAdapterDelegate.class, ARG_SINGLE_CHOICE_ADAPTER);
        if (delegate == null)
            return;

        builder.setSingleChoiceItems(delegate.getListAdapter(this),
                checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final OnItemClickListener listener = findListenerByTag(
                                OnItemClickListener.class, ARG_SINGLE_CHOICE_LISTENER);
                        if (listener != null)
                            listener.onItemClick(AlertDialogFragment.this, which);
                    }
                });
    }

    private void setPositiveButton(AlertDialog.Builder builder) {
        final Bundle args = getArguments();
        final CharSequence positiveButtonText = args.getCharSequence(ARG_POSITIVE_BUTTON);
        if (positiveButtonText == null)
            return;

        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final OnClickListener listener = findListenerByTag(
                        OnClickListener.class, ARG_POSITIVE_BUTTON_LISTENER);
                if (listener != null)
                    listener.onClickPositive(AlertDialogFragment.this);
            }
        });
    }

    private void setNeutralButton(AlertDialog.Builder builder) {
        final Bundle args = getArguments();
        final CharSequence naturalButtonText = args.getCharSequence(ARG_NEUTRAL_BUTTON);
        if (naturalButtonText == null)
            return;

        builder.setNeutralButton(naturalButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final OnClickListener listener = findListenerByTag(
                        OnClickListener.class, ARG_NEUTRAL_BUTTON_LISTENER);
                if (listener != null)
                    listener.onClickNeutral(AlertDialogFragment.this);
            }
        });
    }

    private void setNegativeButton(AlertDialog.Builder builder) {
        final Bundle args = getArguments();
        final CharSequence negativeButtonText = args.getCharSequence(ARG_NEGATIVE_BUTTON);
        if (negativeButtonText == null)
            return;

        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final OnClickListener listener = findListenerByTag(
                        OnClickListener.class, ARG_NEGATIVE_BUTTON_LISTENER);
                if (listener != null)
                    listener.onClickNegative(AlertDialogFragment.this);
            }
        });
    }

    private void setItems(AlertDialog.Builder builder) {
        final Bundle args = getArguments();
        final CharSequence[] items = args.getCharSequenceArray(ARG_ITEMS);
        if (items == null)
            return;

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final OnItemClickListener listener =
                        findListenerByTag(OnItemClickListener.class, ARG_ITEMS_LISTENER);
                if (listener != null)
                    listener.onItemClick(AlertDialogFragment.this, which);
            }
        });
    }

    private void setMultiChoiceItems(AlertDialog.Builder builder) {
        final Bundle args = getArguments();
        final CharSequence[] items = args.getCharSequenceArray(ARG_MULTI_CHOICE_ITEMS);
        final boolean[] checked = args.getBooleanArray(ARG_CHECKED_ITEMS);
        if (items == null || checked == null || items.length != checked.length)
            return;

        builder.setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                final OnMultiItemClickListener listener =
                        findListenerByTag(OnMultiItemClickListener.class, ARG_MULTI_CHOICE_LISTENER);
                if (listener != null)
                    listener.onItemClick(AlertDialogFragment.this, which, isChecked);
            }
        });
    }

    @TargetApi(11)
    private AlertDialog.Builder newDialogBuilder(int theme) {
        return new AlertDialog.Builder(getActivity(), theme);
    }

    private <T> T findListenerByTag(Class<T> clss, String argName) {
        final String target = getArguments().getString(argName);
        if (target == null) {
            return null;
        } else if (TAG_ACTIVITY.equals(target)) {
            return findListener(clss, getActivity());
        } else if (target.startsWith(TAG_FRAGMENT)) {
            return findListener(clss, getFragmentManager().findFragmentByTag(
                    target.substring(TAG_FRAGMENT.length())));
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T findListener(Class<T> clss, Object object) {
        if (object != null && clss.isInstance(object))
            return (T) object;
        return null;
    }

}
