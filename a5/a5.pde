import controlP5.*;

final int DECIDE_YOURSELF = -1; // This is a placeholder for variables you will replace.

/**
 * This is a global variable for the dataset in your visualization. You'll be overwriting it each trial.
 */
Data d = null;
int chartType = 5;

void getNextChart() {
    d = new Data();
    chartType = (int)random(3);
}

ArrayList<Datum> getMarkedDatums() {
  ArrayList<Datum> realDatums = getDatumFromData(d.data);
   ArrayList<Datum> marked = new ArrayList<Datum>();
   
   for (Datum d : realDatums) {
     if (d.marked) {
       marked.add(d);
     } 
   }
   
   return marked;
}

float calculateRealPercent() {
  ArrayList<Datum> marked = getMarkedDatums();
  
  Datum d1 = marked.get(0);
  Datum d2 = marked.get(1);
  
  Datum small = d1.value < d2.value ? d1 : d2;
  Datum large = d1.value < d2.value ? d2 : d1;
  
  return small.value / large.value;
}

void setup() {
    totalWidth = displayWidth;
    totalHeight = displayHeight;
    chartLeftX = totalWidth / 2.0 - chartSize / 2.0;
    chartLeftY = totalHeight / 2.0 - chartSize / 2.0 - margin_top;

    size((int) totalWidth, (int) totalHeight);
    //if you have a Retina display, use the line below (looks better)
    //size((int) totalWidth, (int) totalHeight, "processing.core.PGraphicsRetina2D");

    background(255);
    frame.setTitle("Comp150-07 Visualization, Lab 5, Experiment");

    cp5 = new ControlP5(this);
    pfont = createFont("arial", fontSize, true); 
    textFont(pfont);
    page1 = true;

    /**
     ** Finish this: decide how to generate the dataset you are using (see DataGenerator)
     **/
    getNextChart();

    /**
     ** Finish this: how to generate participant IDs
     ** You can write a short alphanumeric ID generator (cool) or modify this for each participant (less cool).
     **/
    partipantID = 7;
}

ArrayList<Datum> getDatumFromData(Data.DataPoint[] input) {
  ArrayList<Datum> toReturn = new ArrayList();
  
  for (int i = 0; i < NUM; i++) {
    toReturn.add(new Datum(input[i]));
//    println(toReturn.get(i).value);
  }
  
  return toReturn;
  
}

void draw() {
    textSize(fontSize);
    ArrayList<Datum> realDatums = getDatumFromData(d.data);
    Rect bounds = new Rect(chartLeftX, chartLeftY, chartSize, chartSize);
    /**
     ** add more: you may need to draw more stuff on your screen
     **/
    if (index < 0 && page1) {
        drawIntro();
        page1 = false;
    } else if (index >= 0 && index < vis.length) {
        if (index == 0 && page2) {
            clearIntro();
            drawTextField();
            drawInstruction();
            page2 = false;
        }
      
        fill(color(255,255,255));
        stroke(color(255,255,255));
        rect(chartLeftX, chartLeftY, chartSize, chartSize);

        switch (chartType) {
            case 0:
                Bar b = new Bar(realDatums, "", "");
                b.setBounds(bounds);
                b.render();
            
                /**
                 ** finish this: 1st visualization
                 **/
                break;
            case 1:
              PieChart p = new PieChart(realDatums, "", "");
              p.setBounds(bounds);
              p.render();
                /**
                 ** finish this: 2nd visualization
                 **/
                break;
            case 2:
                SQTMDatum root = makeSQTMDatums(realDatums);
                SQTM sqtm = new SQTM(bounds, root);
                sqtm.render();
                break;
            default:
                println("YOU FUCKED UP. type = " + chartType);
                break;
        }

        drawWarning();

    } else if (index > vis.length - 1 && pagelast) {
        drawThanks();
        drawClose();
        pagelast = false;
    }
}

/**
 * This method is called when the participant clicked the "NEXT" button.
 */
public void next() {
    String str = cp5.get(Textfield.class, "answer").getText().trim();
    float num = parseFloat(str);
    /*
     * We check their percentage input for you.
     */
    if (!(num >= 0)) {
        warning = "Please input a number!";
        if (num < 0) {
            warning = "Please input a non-negative number!";
        }
    } else if (num > 100) {
        warning = "Please input a number between 0 - 100!";
    } else {
        if (index >= 0 && index < vis.length) {
            float ans = parseFloat(cp5.get(Textfield.class, "answer").getText());

            /**
             ** Finish this: decide how to compute the right answer
             **/
            truePerc = calculateRealPercent(); // hint: from your list of DataPoints, extract the two marked ones to calculate the "true" percentage

            reportPerc = ans / 100.0; // this is the participant's response
            
            println("true percentage = " + truePerc);
            
            /**
             ** Finish this: decide how to compute the log error from Cleveland and McGill (see the handout for details)
             **/
            error = log2(Math.abs(reportPerc - truePerc) + 1/8.0);

            saveJudgement();
        }

        /**
         ** Finish this: decide the dataset (similar to how you did in setup())
         **/
        getNextChart();

        cp5.get(Textfield.class, "answer").clear();
        index++;

        if (index == vis.length - 1) {
            pagelast = true;
        }
    }
}

float log2(double f) {
  return (float)Math.log(f) / (float)Math.log(2); 
}

/**
 * This method is called when the participant clicked "CLOSE" button on the "Thanks" page.
 */
public void close() {
    /**
     ** Change this if you need to do some final processing
     **/
    saveExpData();
    exit();
}

/**
 * Calling this method will set everything to the intro page. Use this if you want to run multiple participants without closing Processing (cool!). Make sure you don't overwrite your data.
 */
public void reset(){
    /**
     ** Finish/Use/Change this method if you need 
     **/
    partipantID = 7;
    getNextChart();

    /**
     ** Don't worry about the code below
     **/
    background(255);
    cp5.get("close").remove();
    page1 = true;
    page2 = false;
    pagelast = false;
    index = -1;
}
