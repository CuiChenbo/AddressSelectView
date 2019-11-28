package com.ccb.addressselect.bean;

import java.io.Serializable;
import java.util.List;

public class AddressBean implements Serializable {

        private List<AddressItemBean> province;
        private List<AddressItemBean> city;
        private List<AddressItemBean> district;

        public List<AddressItemBean> getProvince() {
            return province;
        }

        public void setProvince(List<AddressItemBean> province) {
            this.province = province;
        }

        public List<AddressItemBean> getCity() {
            return city;
        }

        public void setCity(List<AddressItemBean> city) {
            this.city = city;
        }

        public List<AddressItemBean> getDistrict() {
            return district;
        }

        public void setDistrict(List<AddressItemBean> district) {
            this.district = district;
        }

        public static class AddressItemBean implements Serializable {
            private String id; // 该地区的编号

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAboveId() {
                return aboveId;
            }

            public void setAboveId(String aboveId) {
                this.aboveId = aboveId;
            }

            private String name;
            private String aboveId;//该地区 上一级的编号（如果该地区名叫郑州，那么上一级是河南）
        }
}
