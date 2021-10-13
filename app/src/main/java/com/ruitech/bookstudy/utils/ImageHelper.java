package com.ruitech.bookstudy.utils;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ruitech.bookstudy.bean.Poster;
import com.ruitech.bookstudy.widget.MyViewWare;

import java.util.List;

import androidx.annotation.DimenRes;

public class ImageHelper {

    public static final class Params {
        public List<Poster> posters;
        public String imageUri;
        public @DimenRes int widthDimen;
        public @DimenRes int heightDimen;
        public int width = -1;
        public int height = -1;
        public DisplayImageOptions options;
        public ImageLoadingListener listener;
        
        public static final class Builder {
            private List<Poster> posters;
            private String imageUri;
            private @DimenRes int widthDimen;
            private @DimenRes int heightDimen;
            private int width;
            private int height;
            private DisplayImageOptions options;
            private ImageLoadingListener listener;

            public Builder posters(List<Poster> posters) {
                this.posters = posters;
                return this;
            }

            public Builder imageUri(String imageUri) {
                this.imageUri = imageUri;
                return this;
            }

            public Builder widthDimen(@DimenRes int widthDimen) {
                this.widthDimen = widthDimen;
                return this;
            }

            public Builder heightDimen(@DimenRes int heightDimen) {
                this.heightDimen = heightDimen;
                return this;
            }

            public Builder width(int width) {
                this.width = width;
                return this;
            }

            public Builder height(int height) {
                this.height = height;
                return this;
            }

            public Builder options(DisplayImageOptions options) {
                this.options = options;
                return this;
            }

            public Builder listener(ImageLoadingListener listener) {
                this.listener = listener;
                return this;
            }

            public Params build() {
                Params params = new Params();
                params.posters = posters;
                params.imageUri = imageUri;
                params.widthDimen = widthDimen;
                params.heightDimen = heightDimen;
                params.width = width;
                params.height = height;
                params.options = options;
                params.listener = listener;
                return params;
            }
        }
    }

    public static void displayImage(ImageView imageView, Params params) {
        if (params.width < 0 && params.height < 0) {
            params.width = UIHelper.getDimenPx(params.widthDimen);
            params.height = UIHelper.getDimenPx(params.heightDimen);
        }
        if (params.imageUri == null) {
            params.imageUri = UIUtil.getAppropriatePosterUrl(params.posters, params.width, params.height, true);
        }
        MyViewWare myViewWare = new MyViewWare(imageView, params.width, params.height);
        ImageLoader.getInstance().displayImage(params.imageUri, myViewWare, params.options, params.listener);
    }

    public static void loadImage(Params params) {
        ImageLoader.getInstance().loadImage(params.imageUri, params.options, params.listener);
    }


//    public static void displayImageWithDimen(ImageView imageView, List<Poster> posters, @DimenRes int widthDimen, @DimenRes int heightDimen, DisplayImageOptions options) {
//        int width = UIHelper.getDimenPx(widthDimen);
//        int height = UIHelper.getDimenPx(heightDimen);
//        displayImage(imageView, posters, width, height, options);
//    }

//    public static void displayImage(ImageView imageView, List<Poster> posters, int width, int height, DisplayImageOptions options) {
//        String imageUri = UIUtil.getAppropriatePosterUrl(posters, width, height, true);
//        displayImage(imageView, imageUri, width, height, options, null);
//    }
//
//    public static void displayImage(ImageView imageView, String imageUri, int width, int height, DisplayImageOptions options) {
//        displayImage(imageView, imageUri, width, height, options, null);
//    }

//    public static void displayImage(ImageView imageView, String imageUri, int width, int height, DisplayImageOptions options, ImageLoadingListener listener) {
//        MyViewWare myViewWare = new MyViewWare(imageView, width, height);
//        ImageLoader.getInstance().displayImage(imageUri, myViewWare, options, listener);
//    }

    public static void displayImage(String imageUri, DisplayImageOptions options, ImageLoadingListener listener) {
        ImageLoader.getInstance().loadImage(imageUri, options, listener);
    }
}
