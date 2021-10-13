/*
 * Copyright 2016 drakeet. https://github.com/drakeet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.drakeet.multitype;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruitech.bookstudy.widget.MXAttachable;
import com.ruitech.bookstudy.widget.MXAttachedListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.CheckResult;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import me.drakeet.multitype.animator.AnimatorUtil;
import me.drakeet.multitype.animator.MxViewAnimator;
import me.drakeet.multitype.ext.MultiTypeViewCacheProvider;
import me.drakeet.multitype.ext.ViewCache;

import static android.view.View.TRANSLATION_X;

/**
 * @author drakeet
 */
public class MultiTypeAdapter extends RecyclerView.Adapter<ViewHolder> {
    public static final int INDEX_NOT_FOUND = -1;
    public static boolean DEBUG = false;

    private static final String TAG = "MultiTypeAdapter";

    @Nullable
    private List<?> items;
    @NonNull
    private TypePool typePool;
    @Nullable
    protected LayoutInflater inflater;
    private ViewCache viewCache;
//    private Object contextData;
    private Handler handler = new Handler(Looper.getMainLooper());

//    private Object objContext;

    /**
     * Saved instance state key for the ViewAnimator
     */
    private static final String SAVEDINSTANCESTATE_VIEWANIMATOR = "savedinstancestate_viewanimator";

    /**
     * Alpha property
     */
    private static final String ALPHA = "alpha";

    /**
     * The ViewAnimator responsible for animating the Views.
     */
    @Nullable
    private MxViewAnimator mViewAnimator;
    private @AnimatorType int type = AnimatorType.NONE;

    public static class EmptyViewHolder extends ViewHolder {

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    public void setViewCache(ViewCache viewCache) {
        this.viewCache = viewCache;
    }

    /**
     * Constructs a MultiTypeAdapter with a null items list.
     */
    public MultiTypeAdapter() {
        this(null);
    }

//    public Object contextData() {
//        return contextData;
//    }
//
//    public void setContextData(Object contextData) {
//        this.contextData = contextData;
//    }

    /**
     * Constructs a MultiTypeAdapter with a items list.
     *
     * @param items the items list
     */
    public MultiTypeAdapter(@Nullable List<?> items) {
        this(items, new MultiTypePool());
    }


    /**
     * Constructs a MultiTypeAdapter with a items list and an initial capacity of TypePool.
     *
     * @param items the items list
     * @param initialCapacity the initial capacity of TypePool
     */
    public MultiTypeAdapter(@Nullable List<?> items, int initialCapacity) {
        this(items, new MultiTypePool(initialCapacity));
    }


    /**
     * Constructs a MultiTypeAdapter with a items list and a TypePool.
     *
     * @param items the items list
     * @param pool the type pool
     */
    public MultiTypeAdapter(@Nullable List<?> items, @NonNull TypePool pool) {
        this.items = items;
        this.typePool = pool;
    }

    private LifecycleOwner lifecycleOwner;
    public MultiTypeAdapter lifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        return this;
    }

    /**
     * Registers a type class and its item view binder. If you have registered the class,
     * it will override the original binder(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     * <p>
     * Note that the method should not be called after
     * {@link RecyclerView#setAdapter(RecyclerView.Adapter)}, or you have to call the setAdapter
     * again.
     * </p>
     *
     * @param clazz the class of a item
     * @param binder the item view binder
     * @param <T> the item data type
     */
    public <T> void register(
            @NonNull Class<? extends T> clazz, @NonNull ItemViewBinder<T, ?> binder) {
        checkAndRemoveAllTypesIfNeed(clazz);
        typePool.register(clazz, binder, new DefaultLinker<T>());
    }


    /**
     * Registers a type class to multiple item view binders. If you have registered the
     * class, it will override the original binder(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     * <p>
     * Note that the method should not be called after
     * {@link RecyclerView#setAdapter(RecyclerView.Adapter)}, or you have to call the setAdapter
     * again.
     * </p>
     *
     * @param clazz the class of a item
     * @param <T> the item data type
     * @return {@link OneToManyFlow} for setting the binders
     * @see #register(Class, ItemViewBinder)
     */
    @CheckResult
    public <T> OneToManyFlow<T> register(@NonNull Class<? extends T> clazz) {
        checkAndRemoveAllTypesIfNeed(clazz);
        return new OneToManyBuilder<T>(this, clazz);
    }


    /**
     * Registers all of the contents in the specified type pool. If you have registered a
     * class, it will override the original binder(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     * <p>
     * Note that the method should not be called after
     * {@link RecyclerView#setAdapter(RecyclerView.Adapter)}, or you have to call the setAdapter
     * again.
     * </p>
     *
     * @param pool type pool containing contents to be added to this adapter inner pool
     * @see #register(Class, ItemViewBinder)
     * @see #register(Class)
     */
    public void registerAll(@NonNull final TypePool pool) {
        for (int i = 0; i < pool.getClasses().size(); i++) {
            registerWithoutChecking(
                pool.getClasses().get(i),
                pool.getItemViewBinders().get(i),
                pool.getLinkers().get(i)
            );
        }
    }


    /**
     * Sets and updates the items atomically and safely.
     * It is recommended to use this method to update the data.
     * <p>e.g. {@code adapter.setItems(new Items(changedItems));}</p>
     *
     * <p>Note: If you want to refresh the list views, you should
     * call {@link RecyclerView.Adapter#notifyDataSetChanged()} by yourself.</p>
     *
     * @param items the <b>new</b> items list
     * @since v2.4.1
     */
    public void setItems(@Nullable List<?> items) {
        this.items = items;
    }


    @Nullable
    public List<?> getItems() {
        return items;
    }


    /**
     * Set the TypePool to hold the types and view binders.
     *
     * @param typePool the TypePool implementation
     */
    public void setTypePool(@NonNull TypePool typePool) {
        this.typePool = typePool;
    }


    @NonNull
    public TypePool getTypePool() {
        return typePool;
    }


    @Override
    public final int getItemViewType(int position) {
        assert items != null;
        Object item = items.get(position);
        return indexInTypesOf(item);
    }

    private static Activity unWrapper(Context context) {
        for (int i = 0; i < 5; i++) {
            if (context instanceof Activity)
                return (Activity)context;

            if (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            }
        }

        return null;
    }

    //-----------------------------------------------------------------------------
    // Animators methods
    //-----------------------------------------------------------------------------

    /**
     * Returns the Animators to apply to the views.
     *
     * @param view The view that will be animated, as retrieved by onBindViewHolder().
     */
    public Animator[] getAnimators(@NonNull View view) {
        if (mViewAnimator == null || mViewAnimator.getRecyclerView().getLayoutManager() == null) {
            return null;
        }
        if (type == AnimatorType.SLIDE_IN_RIGHT) {
            return new Animator[]{ObjectAnimator.ofFloat(view, TRANSLATION_X, mViewAnimator.getRecyclerView().getLayoutManager().getWidth(), 0)};
        } else if (type == AnimatorType.EASE_OUT) {
            return new Animator[]{ObjectAnimator.ofFloat(view, TRANSLATION_X, mViewAnimator.getRecyclerView().getLayoutManager().getWidth() * 1.0f / 3, 0)};
        } else {
            return null;
        }
    }

    public void setViewAnimator(@Nullable MxViewAnimator mViewAnimator, @AnimatorType int type) {
        this.mViewAnimator = mViewAnimator;
        this.type = type;
    }

    public long getAnimationEndMillis() {
        if (mViewAnimator != null) {
            return mViewAnimator.getAnimationEndMillis();
        }
        return -1;
    }

    /**
     * Animates given View
     *
     * @param position the position of the item the View represents.
     * @param view     the View that should be animated.
     */
    private void animateView(final View view, final int position) {
        if (mViewAnimator == null) {
            return;
        }

        Animator[] animators = getAnimators(view);
        if (animators == null) {
            return;
        }
        Animator alphaAnimator = ObjectAnimator.ofFloat(view, ALPHA, 0, 1);
        Animator[] concatAnimators = AnimatorUtil.concatAnimators(animators, alphaAnimator);
        mViewAnimator.animateViewIfNecessary(position, view, concatAnimators);
    }

    @Nullable
    public MxViewAnimator getViewAnimator() {
        return mViewAnimator;
    }

    //-----------------------------------------------------------------------------
    // SaveInstanceState
    //-----------------------------------------------------------------------------

    /**
     * Returns a Parcelable object containing the AnimationAdapter's current dynamic state.
     */
    @NonNull
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        if (mViewAnimator != null) {
            bundle.putParcelable(SAVEDINSTANCESTATE_VIEWANIMATOR, mViewAnimator.onSaveInstanceState());
        }

        return bundle;
    }

    /**
     * Restores this AnimationAdapter's state.
     *
     * @param parcelable the Parcelable object previously returned by {@link #onSaveInstanceState()}.
     */
    public void onRestoreInstanceState(@Nullable final Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            Bundle bundle = (Bundle) parcelable;
            if (mViewAnimator != null) {
                mViewAnimator.onRestoreInstanceState(bundle.getParcelable(SAVEDINSTANCESTATE_VIEWANIMATOR));
            }
        }
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int indexViewType) {
//        long now = SystemClock.elapsedRealtime();
        if (indexViewType == INDEX_NOT_FOUND) {
            View itemView = new View(parent.getContext());
            if (DEBUG)
                itemView.setBackgroundColor(Color.BLUE);
            return new EmptyViewHolder(itemView);
        }

        ItemViewBinder<?, ?> binder = typePool.getItemViewBinders().get(indexViewType);
        binder.adapter = this;

        if (viewCache == null) {
            Context context = parent.getContext();
            Activity activity = unWrapper(context);
            if (activity instanceof MultiTypeViewCacheProvider) {
                viewCache = ((MultiTypeViewCacheProvider) activity).getMultiTypeViewCache();
                this.inflater = viewCache.getInflater();
            }
        }

        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        int layoutId = binder.getMultiTypeLayoutId();
        if (layoutId != 0 && viewCache != null) {
            View view = viewCache.getViewById(layoutId, binder);
            if (view != null) {
                ViewHolder holder = binder.onCreateViewHolder(inflater, parent, view);
//                if (holder instanceof MXViewHolder) {
//                    ((MXViewHolder) holder).setObjContext(objContext);
//                }
                if (lifecycleOwner != null && holder instanceof LifecycleEventObserver) {
                    lifecycleOwner.getLifecycle().addObserver((LifecycleObserver) holder);
                }
                return holder;
            }
        }

        ViewHolder holder = binder.onCreateViewHolder(inflater, parent);
//        if (holder instanceof MXViewHolder) {
//            ((MXViewHolder) holder).setObjContext(objContext);
//        }
//        if (layoutId != 0) {
//            holder.itemView.setTag(R.id.multi_type_tag, layoutId);
//        }
        if (lifecycleOwner != null && holder instanceof LifecycleEventObserver) {
            lifecycleOwner.getLifecycle().addObserver((LifecycleObserver) holder);
        }
        return holder;
    }

    /**
     * This method is deprecated and unused. You should not call this method.
     * <p>
     * If you need to call the binding, use {@link RecyclerView.Adapter#onBindViewHolder(ViewHolder,
     * int, List)} instead.
     * </p>
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     * @throws IllegalAccessError By default.
     * @deprecated Call {@link RecyclerView.Adapter#onBindViewHolder(ViewHolder, int, List)}
     * instead.
     */
    @Override @Deprecated
    public final void onBindViewHolder(ViewHolder holder, int position) {
        throw new IllegalAccessError("You should not call this method. " +
            "Call RecyclerView.Adapter#onBindViewHolder(holder, position, payloads) instead.");
    }


    @Override @SuppressWarnings("unchecked")
    public final void onBindViewHolder(final ViewHolder holder, int position, List<Object> payloads) {
        assert items != null;
//        long now = SystemClock.elapsedRealtimeNanos();
//        if (holder instanceof MXViewHolder) {
//            ((MXViewHolder) holder).onBind();
//        }
        final int itemViewType = holder.getItemViewType();
        if (itemViewType == INDEX_NOT_FOUND) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    int adapterPosition = holder.getAdapterPosition();
                    if  (adapterPosition != -1) {
                        Object obj = items.get(adapterPosition);
                        if (isNotSupported(obj)) {
                            items.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                        }
                    }
                }
            });
            return;
        }
        Object item = items.get(position);
        ItemViewBinder binder = typePool.getItemViewBinders().get(itemViewType);
        binder.onBindViewHolder(holder, item, payloads);

        if (mViewAnimator != null) {
            mViewAnimator.cancelExistingAnimation(holder.itemView);
            animateView(holder.itemView, position);
        }
    }

    @Override
    public final int getItemCount() {
        return items == null ? 0 : items.size();
    }

    private boolean isNotSupported(Object item) {
        return typePool.firstIndexOf(item.getClass()) == -1;
    }

    int indexInTypesOf(@NonNull Object item) throws BinderNotFoundException {
        int index = typePool.firstIndexOf(item.getClass());
        if (index != -1) {
            try {
            @SuppressWarnings("unchecked")
            Linker<Object> linker = (Linker<Object>) typePool.getLinkers().get(index);
            return index + linker.index(item);
            } catch (BinderNotFoundException ignore) {
            }
        }
        onBinderNotFound(item);

        return INDEX_NOT_FOUND;
    }

    private void onBinderNotFound(Object item) throws BinderNotFoundException {
//        ExceptionUtil.handleException(new BinderNotFoundException(createOnBinderNotFoundString(item), item.getClass()));
    }

    protected String createOnBinderNotFoundString(Object item) {
        Set<Class> types = new HashSet<>();
        for (int i = 0; i < getItemCount(); i++) {
            types.add(items.get(i).getClass());
        }

        StringBuilder stringBuilder = new StringBuilder(typePool.toString());
        stringBuilder.append("dataType: ");
        for (Class type : types) {
            String simpleName = type.getSimpleName();
            stringBuilder.append(simpleName);
            stringBuilder.append(",");
        }

        stringBuilder.append("\n");

        return stringBuilder.toString();
    }


    private void checkAndRemoveAllTypesIfNeed(@NonNull Class<?> clazz) {
        if (!typePool.getClasses().contains(clazz)) {
            return;
        }
        Log.w(TAG, "You have registered the " + clazz.getSimpleName() + " type. " +
            "It will override the original binder(s).");
        for (; ; ) {
            int index = typePool.getClasses().indexOf(clazz);
            if (index != -1) {
                typePool.getClasses().remove(index);
                typePool.getItemViewBinders().remove(index);
                typePool.getLinkers().remove(index);
            } else {
                break;
            }
        }
    }


    <T> void registerWithLinker(
        @NonNull Class<? extends T> clazz,
        @NonNull ItemViewBinder<T, ?> binder,
        @NonNull Linker<T> linker) {
        typePool.register(clazz, binder, linker);
    }


    /** A safe register method base on the TypePool's safety for TypePool. */
    @SuppressWarnings("unchecked")
    private void registerWithoutChecking(
            @NonNull Class clazz, @NonNull ItemViewBinder itemViewBinder, @NonNull Linker linker) {
        checkAndRemoveAllTypesIfNeed(clazz);
        typePool.register(clazz, itemViewBinder, linker);
    }

//    /**
//     * use for adapter's holder to know current view is visible or not or create or destroy.
//     * @param objContext fragment or activity.
//     */
//    public void setObjContext(Object objContext) {
//        this.objContext = objContext;
//    }

//    public Object getObjContext() {
//        return objContext;
//    }

//    public abstract static class MXViewHolder extends ViewHolder implements MXAttachedListener {
//        private boolean detached;
//        private boolean bind;
//        private boolean called;
//        protected Object objContext;

//        public MXViewHolder(View itemView) {
//            super(itemView);
//            if (itemView instanceof MXAttachable) {
//                ((MXAttachable) itemView).addAttachedListener(this);
//            }
//        }
//
//        public void onAttached() {
//
//        }

//        protected boolean rebind() {
//            return true;
//        }

//        private void dispatchOnDetached() {
//            Log.e(TAG, "dispatchOnDetached");
//            called = false;
//            onDetached();
//
//            if (!called) {
//                throw new IllegalStateException("super.onDetached() should be called.");
//            }
//        }
//        private void dispatchOnAttached() {
//            Log.e(TAG, "dispatchOnAttached");
//            called = false;
//            onAttached();
//
//            if (!called) {
//                throw new IllegalStateException("super.onAttached() should be called.");
//            }
//        }

//        public void onDetached() {
//
//        }
//
//        @Override
//        public final void onAttachedToWindow() {
//            onAttached();
//        }
//
//        @Override
//        public final void onDetachedFromWindow() {
//            onDetached();
//        }

//        /**
//         * @return  fragment or activity.
//         *
//         * {@link MultiTypeAdapter#setObjContext(Object)}}
//         */
//        public final void setObjContext(Object objContext) {
//            this.objContext = objContext;
//        }
//    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            AnimatorType.NONE,
            AnimatorType.SLIDE_IN_RIGHT,
            AnimatorType.EASE_OUT
    })
    public @interface AnimatorType {
        int NONE = 0;
        int SLIDE_IN_RIGHT = 1;
        int EASE_OUT = 2;
    }

//    public interface IObjContextProvider {
//        /**
//         * @return  fragment or activity.
//         *
//         * {@link MultiTypeAdapter#setObjContext(Object)}}
//         */
//        Object objContext();
//    }
}
