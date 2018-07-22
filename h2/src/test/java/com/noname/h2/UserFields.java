package com.noname.h2;

import com.noname.data.Entity;
import com.noname.data.Field;

import java.math.BigDecimal;


public enum UserFields implements Field {
    ID {
        @Override
        public String getName() {
            return this.name().toLowerCase();
        }

        @Override
        public Class<?> getType() {
            return Long.class;
        }


        @Override
        public boolean isPK() {
            return true;
        }
    },


    LOGIN {
        @Override
        public String getName() {
            return this.name().toLowerCase();
        }

        @Override
        public Class<?> getType() {
            return String.class;
        }


    },

    PASSWORD {
        @Override
        public String getName() {
            return this.name().toLowerCase();
        }

        @Override
        public Class<?> getType() {
            return String.class;
        }
    },

    BALANCE {
        @Override
        public String getName() {
            return this.name().toLowerCase();
        }

        @Override
        public Class<?> getType() {
            return BigDecimal.class;
        }
    };


    @Override
    public Class<? extends Entity> getEntityClass() {
        return User.class;
    }
}
