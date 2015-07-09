package com.meitu.android.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: wl
 * @Email : wangliang@daoxila.com
 * @Date : 2015/06/18 20:33
 * @Description: {描述这个类的作用}
 * @Copyright(c) 2015 www.daoxila.com. All rights reserved.
 */
public class ImageListByAlbumIdModel{

    private List<AlbumModel> albumModelList;
    private Info info;

    public List<AlbumModel> getAlbumModelList() {
        return albumModelList;
    }

    public void setAlbumModelList(List<AlbumModel> albumModelList) {
        this.albumModelList = albumModelList;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public static class AlbumModel {
        private String source;
        private String url;
        private String id;
        private String mid_image;
        private String images_id;
        private String small_image;
        private String big_image;
        private String title;


        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMid_image() {
            return mid_image;
        }

        public void setMid_image(String mid_image) {
            this.mid_image = mid_image;
        }

        public String getImages_id() {
            return images_id;
        }

        public void setImages_id(String images_id) {
            this.images_id = images_id;
        }

        public String getSmall_image() {
            return small_image;
        }

        public void setSmall_image(String small_image) {
            this.small_image = small_image;
        }

        public String getBig_image() {
            return big_image;
        }

        public void setBig_image(String big_image) {
            this.big_image = big_image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }

    public static class Info{
        private String pre;
        private String next;
        private String up_num;
        private String category;

        public String getPre() {
            return pre;
        }

        public void setPre(String pre) {
            this.pre = pre;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public String getUp_num() {
            return up_num;
        }

        public void setUp_num(String up_num) {
            this.up_num = up_num;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }

}
