package com.kotling.display

enum class BlendMode {
    AUTO {
        override fun activate() {
        }
    },

    NORMAL {
        override fun activate() {
        }
    },

    ADD {
        override fun activate() {
        }
    },

    MULTIPLY {
        override fun activate() {
        }
    };

    abstract fun activate()
}
