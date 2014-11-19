class Data {
    class DataPoint {
        private float value = -1;
        private boolean marked = false;

        DataPoint(float f, boolean m) {
            this.value = f;
            this.marked = m;
        }

        boolean isMarked() {
            return marked;
        }

        void setMark(boolean b) {
            this.marked = b;
        }

        float getValue() {
            return this.value;
        }
    }

    private DataPoint[] data = null;

    Data() {
        // NUM is a global varibale in support.pde
        data = new DataPoint[NUM];
        
        for (int i = 0; i < NUM; i++) {
          data[i] = new DataPoint(random(101), false);
        }
        
        // Pick marked
        int firstInd = (int)random(NUM);
        int secondInd;
        data[firstInd].marked = true;
        
        do {
          secondInd = (int)random(10);
        }
        while (secondInd == firstInd);
        
        data[secondInd].marked = true;
    }
}
