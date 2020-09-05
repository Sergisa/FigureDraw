import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import g4p_controls.*;
import java.awt.Font;
import javax.swing.JOptionPane;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import processing.serial.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class DueAWGControllerLinux extends PApplet {

    // used for creating Options window

    // used for creating message boxes & help window





    Serial SerialData;  // Create object from Serial class
    boolean MouseJustPressed;
    int     Magnify = 3; // magnifies the program on the PC screen: 1 = drawing area space on screen to create a 300 waypoint wave (300 screen pixels), 5 = 1500 waypoints (pixels)
    int     OptionsMag = 0; // 0 = Options window is closed, saves Magnify value while Options window is open
    int     KeypadPos = 0; // position of Keypad etc: 0 = top, 1 = bottom
    int     Mode;
    BufferedReader Reader;
    int     ArbitraryPointNumber = 300 * Magnify; // number of waypoints found in wave
    int[] ArbitraryWave = new int [4097];
    int[] ArbitraryWaveStep = new int [4097];
    boolean Wave0Filled;
    boolean WaveStep0Filled;
    int     Saving;
    byte    DrawnArbitrarySetup; // 0 = re-draw start & end markers & clear drawing area, 1 = clear drawing area only, 2 = drawn
    boolean ProgDraw = false;   // 0 = free-hand draw, 1 = progressive draw
    boolean Scrolling;
    int     ScrollTime;
    byte    Autofill = 1; // 0 = manual fill, 1 = autofill, 2 = erase
    boolean Drawing;      // if started drawing (cancelled by clear button or when drawn)
    byte    DrawWave = 0; // 0 = don't draw, 1 = draw, 2 = drawn
    int     TimerTouchTime = 0; // (in timer mode)
    int     TouchTime = 0;
    int     InfoTime = 0;
    int     MinX = 20; // minimum value for x when drawing
    int     TempX;
    int     Oldx;
    int     Oldy;
    int     EditRedraw = 1; // 1 = automatically redraw after editing (erasing)
    int     EditSize = 10;  // size of erase tool tip
    boolean SquareWaveSync; // if square wave is sync'ed with analog wave
    GWindow OptionsWindow;
    GWindow HelpWindow;
    int     SelectedSubject = -1;
    int     HelpLoopNum;
    GCustomSlider sdr1; // Options window slider
    GToggleGroup tg = new GToggleGroup(); // Options window radio buttons
    GOption opt1, opt2, opt3, opt4, opt5;
    GCheckbox cbx0;  // Options window checkbox
    GCheckbox cbx1;  // Options window checkbox
    GTextField txf1; // User Input text field
    GTextField txf2; // Progressive Step size text field
    String ProgStep = "2"; // Progressive Step size
    boolean Connected = false; // if connected to Arduino
    String  UserInput = "50";
    String  SendControl = ""; // Control signal to send to Arduino before QuantifiedInput (when needed)
    String  QuantifiedInput = "0"; // setting data sent to Arduino in Modes 0 & 1
    float[] SlideSetting = new float[2]; // setting data sent to Arduino in Mode 2 (instead of QuantifiedInput)
    String  Delimiter; // delimiter sent to Arduino after QuantifiedInput
    boolean ExactFreqMode = false;
    String  OpenedWave;
    int     SweepStep; // 0 = Freq sweep off, 1-5 = sweep setup, Over 5 = sweep running
    String  SweepMinFreq = "20 Hz";
    String  SweepMaxFreq = "20 kHz";
    float   SweepMinFreqf = 20;
    float   SweepMaxFreqf = 20000;
    float   SweepRiseTime = 20;
    float   SweepFallTime = 20;
    String  Info; // info sometimes displayed above keypad area
    String  AnalogTargetFreq = "1.00000 kHz";
    String  AnalogActualFreq = "1.00000 kHz";
    String  SqWaveTargetFreq = "1.00000 kHz";
    String  SqWaveActualFreq = "1.00000 kHz";
    String  AnalogTargetDuty = "50.00 %";
    String  AnalogActualDuty = "50.00 %";
    String  SqWaveTargetDuty = "50.00 %";
    String  SqWaveActualDuty = "50.00 %";
    String  AnalogTargetPeriod = "Not Set";
    String  AnalogActualPeriod = "1.00000 mS";
    String  SqWaveTargetPeriod = "Not Set";
    String  SqWaveActualPeriod = "1.00000 mS";
    String  AnalogTargetPulseW = "Not Set";
    String  AnalogActualPulseW = "500.00000 µS";
    String  SqWaveTargetPulseW = "Not Set";
    String  SqWaveActualPulseW = "500.00000 µS";
    StringList Descriptor1;
    StringList Quantifier1;
    String  Descriptor = "Hz";
    String  Quantifier = " k";
    int     InputMultiplier = 2;
    int     RememberInputMultiplier = 2;
    byte    Symbol = 1;    // 0 = S   1 = Hz   2 = %
    byte[]  SettingTouched = new byte[3]; // for mode 1 (& 2). [0] is for synchronized wave. [1] is for unsynchronized wave. 0 = off, 1 = freq, 2 = period, 3 = duty-cycle, 4 = pulse width
    byte    WaveShape = 0;       // 0 = Sinewave, 1 = Triangle / Sawtooth, 2 = Arbitrary
    int     TimerMode = 0;      // 0 = timer off, 1 = timer on
    boolean TimerInvert;       // sets timer output neg instead of pos
    boolean TimerRun = false; // reset or start timer running
    int     TimeUp;          // state changes when time entered has passed
    int     TimerInput;     // User Input for timer
    int     TimerSecs;     // time that has passed since resetting timer
    int     TimerMins;
    int     TimerHours;
    int     TimerDays;
    int     PeriodS;  // seconds - Target time period
    int     PeriodM; // minutes
    int     PeriodH; // hours
    int     PeriodD; // days
    int     StartTime;

    public void settings()
    {
        String filename = dataPath("Settings.awg"); // in data folder
        File file = new File(filename);
        if (file.exists())
        {
            println("Reading from Settings file.");
            // Read from Settings file:
            String[] data;
            data = loadStrings(filename);
            int lineNum = 1; // ignore line 0
            while (lineNum < data.length)
            {
                String[] pieces = split(data[lineNum], '\t');
                if (pieces.length == 2)
                {
                    // don't read first part of line: int(pieces[0]);
                    if (lineNum == 1) Magnify    = PApplet.parseInt(pieces[1]);
                    if (lineNum == 2) EditSize   = PApplet.parseInt(pieces[1]);
                    if (lineNum == 3) EditRedraw = PApplet.parseInt(pieces[1]);
                    if (lineNum == 4) ProgStep   = pieces[1];
                    if (lineNum == 5) KeypadPos = PApplet.parseInt(pieces[1]);
                }
//      println(pieces[0] + pieces[1]);
                lineNum++;
            }
        }
        else
        {
            println("The settings file doesn't exist. Will create it...");
            SaveSettingsFile();
        }
        ArbitraryPointNumber = 300 * Magnify; // number of waypoints found in wave
        size((300 * Magnify) + 400, max(470, (175 * Magnify) + 120)); // size of program window in pixels
    }

    public void setup()
    {
        Descriptor1 = new StringList(); // source
        Descriptor1.append("S");
        Descriptor1.append("Hz");
        Descriptor1.append("%");
        Quantifier1 = new StringList(); // source
        Quantifier1.append(" n");
        Quantifier1.append(" µ");
        Quantifier1.append("m");
        Quantifier1.append("  ");
        Quantifier1.append(" k");
        Quantifier1.append("M");

        if (KeypadPos > 0) KeypadPos = Magnify;
        for(int i = 0; i <= 300 * Magnify; i++)
        {
            ArbitraryWave[i] = -1; // make all points = -1 to indicate they're not set
            ArbitraryWaveStep[i] = -1; // resets any stepped points to 'unstepped' state
        }
        ArbitraryWave[0] = 2047; // start of wave at centre value
        ArbitraryWave[300 * Magnify] = 2047; // just past end of wave at centre value
        background(40);
        textSize(15);
        text("OPEN", 18, 22);
        text("DELETE", Magnify * 97, 22);
        text("SAVE", (Magnify * 197) - 6, 22);
        text("UPLOAD", (Magnify * 300) - 35, 22);
        text("OPTIONS", (Magnify * 300) + 60, 22);
        text("HELP", (Magnify * 300) + 160, 22);
        fill(180);
        text((Magnify * 300) + " waypoints in window", 18, (Magnify * 175) + 75);
        fill(255);
        text("CLEAR", 18, (Magnify * 175) + 104);
        text(" FREE-HAND  ", (Magnify * 97) - 9, (Magnify * 175) + 104);
        text("AUTOFILL", (Magnify * 197) + 7, (Magnify * 175) + 104);
        text("FILL", (Magnify * 300) - 5, (Magnify * 175) + 104);
        text("MODE", (Magnify * 300) + 60, max(454, (175 * KeypadPos) + 104));
        text("FREQUENCY SWEEP", (Magnify * 300) + 144, max(454, (175 * KeypadPos) + 104));
        text("TIMER", (Magnify * 300) + 323, max(454, (175 * KeypadPos) + 104));
        noFill();
        stroke(5);
        line(18, 33, (300 * Magnify) + 21, 33); // top
        point(18, 34); // top left
        line(17, 54, (300 * Magnify) + 22, 54); // top outside drawing area border
        line(20, (175 * Magnify) + 58, (300 * Magnify) + 20, (175 * Magnify) + 58); // bottom inside drawing area border
        line(18, 79 + (175 * Magnify), (300 * Magnify) + 21, 79 + (175 * Magnify)); // bottom
        point(18, 80 + (175 * Magnify)); // bottom left
        stroke(70);
        line(19, 35, (300 * Magnify) + 22, 35); // top
        point((300 * Magnify) + 22, 34); // top right
        line(19, 56, (300 * Magnify) + 20, 56); // top inside drawing area border
        line(18, (175 * Magnify) + 60, (300 * Magnify) + 23, (175 * Magnify) + 60); // bottom outside drawing area border
        line(19, 81 + (175 * Magnify), (300 * Magnify) + 22, 81 + (175 * Magnify)); // bottom
        point((300 * Magnify) + 22, 80 + (175 * Magnify)); // bottom right
        txf1 = new GTextField(this, (300 * Magnify) + 123, max(366, (175 * KeypadPos) + 15) + 3, 90, 25);
        txf1.setFont(new Font("Dialog", Font.PLAIN, 18));
        txf1.setText(UserInput);
        text("Searching for Arduino", (300 * Magnify) + 217, 22);
        for (int i = 0; i < Serial.list().length; i++)
        {
            try
            {
                println("\nChecking [" + i + "] " + Serial.list()[i]);
                SerialData = new Serial(this, Serial.list()[i], 115200);
                delay(550);
                if (SerialData.available() > 0) // && Connected)
                {
                    delay(5);
                    String data = SerialData.readStringUntil('D');
                    //     println("data is: " + data);
                    data = data.substring(data.length() - 8);
                    if (data.equals("****** D"))
                    {
                        fill(40);
                        noStroke();
                        rect((300 * Magnify) + 215, 10, 170, 15);
                        fill(255);
                        //         text("Arduino found (" + Serial.list()[i] + ")", (300 * Magnify) + 225, 22);
                        String end = Serial.list()[i].substring(Serial.list()[i].length() - min(Serial.list()[i].length(), 7));
                        text("Connected: (" + end + ")", (300 * Magnify) + 227, 22);
                        delay(200);
                        print(" ... Arduino found\n\n\n     ************** D");
                        Delimiter = "G"; // informs Arduino that the GUI is connected
                        Connected = true;
                        break;
                    }
                    else
                    {
                        println(data + " ... data is not from Due Waveform Generator ");
                        text("Can't find Arduino!", (300 * Magnify) + 240, 22);
                    }
                }
                else
                {
                    if (i == Serial.list().length)
                    {
                        fill(40);
                        noStroke();
                        rect((300 * Magnify) + 215, 10, 170, 15);
                        fill(255);
                        println(" ... Can't find Arduino");
                        text("Can't find Arduino!", (300 * Magnify) + 240, 22); // if last iteration (last com port)
                    }
                }
            }
            catch (Exception e)
            {
                println(" ... Error, com-port busy?");
//      text("Error, com-port busy?", (300 * Magnify) + 226, 22); // if last iteration (last com port)
                e.printStackTrace();
            }
        }
        if (!Connected) // if not connected automatically, try to connect manually:
        {
            String COMx, COMlist = "";
//    if (Serial.list().length > 0) SerialData.stop(); // changed
            int i = Serial.list().length;
//    println("\nPorts = " + i);
            try
            {
                if (i > 0)
                {
                    for (int j = 0; j < min(26, i); j++) // create a list of available com ports
                    {
                        COMlist += PApplet.parseChar(j+'a') + " = " + Serial.list()[j];
                        if ((j + 8) % 7 == 0) COMlist += ",\n";
                        else if (j < min(25, i - 1)) COMlist += ",   ";
                    }
                    COMx = JOptionPane.showInputDialog("Cannot connect automatically.\n\nYour PC has recognized the following ports:  \n\n" + COMlist + "\n\nWhich port is correct?  (Type:  a  or  b  etc)");
                    fill(40);
                    noStroke();
                    rect((300 * Magnify) + 215, 10, 170, 15);
                    fill(255);
                    if (COMx == null || COMx.isEmpty()) text("Can't find Arduino!", (300 * Magnify) + 240, 22);
                    else
                    {
                        i = PApplet.parseInt(COMx.toLowerCase().charAt(0) - 'a') + 1;
                        println("\nPort manually selected is " + Serial.list()[i - 1]);
                        SerialData = new Serial(this, Serial.list()[i-1], 115200);
//          text("Arduino found (" + Serial.list()[i-1] + ")", (300 * Magnify) + 225, 22);
                        String end = Serial.list()[i - 1].substring(Serial.list()[i - 1].length() - min(Serial.list()[i - 1].length(), 7));
                        text("Connected: (" + end + ")", (300 * Magnify) + 227, 22);
                        Delimiter = "G"; // informs Arduino that the GUI is connected
                        Connected = true;
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(frame,"Your PC has not recognized any USB device"); // if (i == 0)
                    fill(40);
                    noStroke();
                    rect((300 * Magnify) + 215, 10, 170, 15);
                    fill(255);
                    text("Can't find Arduino!", (300 * Magnify) + 240, 22);
                }
            }
            catch (Exception e)
            {
                println("Error:");
                fill(40);
                noStroke();
                rect((300 * Magnify) + 215, 10, 170, 15);
                fill(255);
                text("Can't find Arduino!", (300 * Magnify) + 240, 22);
                e.printStackTrace();
            }
        }
    }

//=======================================================================================================
//                                 OPTIONS WINDOW:

    public void createOptionsWindow()
    {
        OptionsWindow = GWindow.getWindow(this, "Options", 220, 160, 350, 260, JAVA2D);
        OptionsWindow.addDrawHandler(this, "OptionsWindowDraw");
//  OptionsWindow.addMouseHandler(this, "OptionsWindowMouse");
        OptionsWindow.setActionOnClose(G4P.CLOSE_WINDOW);
        OptionsWindow.addOnCloseHandler(this, "OptionsWindow_close");
        // Slider:
        sdr1 = new GCustomSlider(OptionsWindow, 195, 72, 100, 50, null);
        // show           opaque ticks value limits
        sdr1.setShowDecor(false, true, true, true);
        sdr1.setNbrTicks(5);
        sdr1.setLimits(EditSize, 1, 99);
        // Options:                        x   y   w   h
        opt1 = new GOption(OptionsWindow, 65, 59, 50, 16, "  300");
        opt2 = new GOption(OptionsWindow, 65, 79, 50, 16, "  600");
        opt3 = new GOption(OptionsWindow, 65, 99, 50, 16, "  900");
        opt4 = new GOption(OptionsWindow, 65, 119, 50, 16, "1200");
        opt5 = new GOption(OptionsWindow, 65, 139, 50, 16, "1500");
        tg.addControls(opt1, opt2, opt3, opt4, opt5);
        if      (Magnify == 1) opt1.setSelected(true);
        else if (Magnify == 2) opt2.setSelected(true);
        else if (Magnify == 3) opt3.setSelected(true);
        else if (Magnify == 4) opt4.setSelected(true);
        else if (Magnify == 5) opt5.setSelected(true);
        // Checkbox:
        cbx0 = new GCheckbox(OptionsWindow, 209, 126, 80, 25, " Re-draw");
        cbx0.setSelected(EditRedraw == 1);
        cbx1 = new GCheckbox(OptionsWindow, 55, 205, 245, 25, " Keypad always at bottom of window");
        cbx1.setSelected(KeypadPos > 0);
        txf2 = new GTextField(OptionsWindow, 223, 178, 30, 19);
        txf2.setFont(new Font("Dialog", Font.PLAIN, 14));
        txf2.setText(ProgStep);
        TempX = KeypadPos; // save value before entering setup
        OptionsMag = Magnify;  // save value before entering setup
//  println("entering setup, Magnify saved as: " + OptionsMag);
    }

/*public void OptionsWindowMouse(PApplet appc, GWinData data, MouseEvent event)
{
  // not used
}*/

    public void OptionsWindowDraw(PApplet appc, GWinData data) // Handles drawing to the windows PApplet area
    {
        appc.background(230, 230, 220);
        appc.stroke(190);
        appc.noFill();
        appc.rect(40, 35, 100, 130); // waypoints
        appc.rect(180, 35, 130, 130); // erase
        appc.stroke(230, 230, 220);
        appc.line(45, 35, 135, 35); // waypoints
        appc.line(192, 35, 298, 35); // erase
        appc.fill(0, 0, 120);
        appc.text(" Waypoints in\nDrawing Area", 49, 34);
        appc.text(" Waypoints in\nDrawing Area", 49, 34);
        appc.text("Erasing Options", 198, 38);
        appc.text("Erasing Options", 198, 38);
        appc.text("Tip Size", 223, 68);
        appc.text("Progressive Step Size:", 86, 193);
        if (Magnify != OptionsMag || KeypadPos != TempX) appc.text("This change would require a restart of the program.", 15, 245); // if changed since entering setup
        try
        {
            if (txf2.getText() != null && Float.isNaN(PApplet.parseFloat(trim(txf2.getText())))) appc.text("Invalid", 258, 194);
        }
        catch (Exception e)
        {
//    e.printStackTrace();
        }
    }

    public void handleSliderEvents(GValueControl slider, GEvent event) // for Options window
    {
//  println("custom_slider1 - GCustomSlider >> GEvent." + event + " @ " + millis());
//  println("Slider value:" + slider.getValueI()); // + " float value:" + slider.getValueF());
        EditSize = slider.getValueI();
    }

    public void handleToggleControlEvents(GToggleControl option, GEvent event) // for Options window
    {
//  println("Option >> GEvent." + option + " @ " + millis());
        if (option == cbx0) // if checkbox clicked
        {
            if (cbx0.isSelected()) EditRedraw = 1;
            else EditRedraw = 0;
//    println("EditRedraw = " + EditRedraw);
        }
        else if (option == cbx1) // if checkbox clicked
        {
            if (cbx1.isSelected()) KeypadPos = Magnify;
            else KeypadPos = 0;
//    println("KeypadPos = " + KeypadPos);
        }
        else // if radio buttons clicked
        {
            if      (option == opt1) Magnify = 1;
            else if (option == opt2) Magnify = 2;
            else if (option == opt3) Magnify = 3;
            else if (option == opt4) Magnify = 4;
            else if (option == opt5) Magnify = 5;
//    println("Magnify = " + Magnify);
        }
    }

    public void OptionsWindow_close(GWindow OptionsWindow)
    {
        if (Magnify != OptionsMag || KeypadPos != TempX) // if changed, must exit app for change to take effect
        {
            JOptionPane.showMessageDialog(frame, "You will need to restart the program \n before your change can take effect.");
        }
        SaveSettingsFile();
        KeypadPos = TempX; // as not exiting app, return to normal value
        Magnify = OptionsMag; // as not exiting app, return to normal value
        OptionsMag = 0;
        OptionsWindow = null;
    }

//=======================================================================================================
//                                 HELP WINDOW:

    public void createHelpWindow()
    {
        HelpWindow = GWindow.getWindow(this, "Help", 100, 50, 640, 640, JAVA2D);
        HelpWindow.addDrawHandler(this, "HelpWindowDraw");
        HelpWindow.addMouseHandler(this, "HelpWindowMouse");
        HelpWindow.setActionOnClose(G4P.CLOSE_WINDOW);
        HelpWindow.addOnCloseHandler(this, "HelpWindow_close");
        if (SelectedSubject < 0) SelectedSubject *= -1; // so 2nd help window can't be opened & selected subject remembered
    }

    public void HelpWindowMouse(PApplet appc, GWinData data, MouseEvent event)
    {
        if (event.getAction() == MouseEvent.PRESS && appc.mouseX < 160 && appc.mouseY > 110)
        {
            MouseJustPressed = true;
            HelpLoopNum = 0;
//    println("x = " + appc.mouseX + " y = " + appc.mouseY);
            appc.loop();
        }
    }

    public void HelpWindowDraw(PApplet appc, GWinData data) // Handles drawing to the windows PApplet area
    {
        appc.background(230, 230, 220);
        appc.fill(0, 0, 120);
        appc.textSize(18);
        appc.text("Arduino Due Arbitrary Waveform Generator Controller\n                                    Help", 85, 28); // Main Header
        appc.textSize(15);
        appc.text("CONTENTS:", 35, 96);
        if (HelpLoopNum == 1) // if 2nd time thru help draw loop, because processing only draws after 2nd (appc)loop! (this prevents reading the file twice)
        {
            try
            {
                String[] lines = loadStrings("Help.txt");
                TouchTime = millis();
                int sp = 0;
                for (int i = 0; i < lines.length; i++) // find & print Contents
                {
                    if (Float.isNaN(PApplet.parseFloat(lines[i]))) continue; // if line is not a number, go to next line
                    else
                    {
                        appc.text(lines[i + 1], 20, 126 + sp); // print Contents (as shown on line following number line)
                        sp = sp + 20;
                    }
                }
                if (appc.mouseX < 160 && appc.mouseY > 110 && appc.mouseY < 330)
                {
                    SelectedSubject = max(0, appc.mouseY - 90) / 20;
                }
                int subject = 0;
                sp = 0;
                for (int i = 0; subject <= SelectedSubject && i < lines.length; i++) // find & print selected Subject:
                {
                    if (subject == SelectedSubject)
                    {
                        if (sp == 0) sp = i;
                        if (Float.isNaN(PApplet.parseFloat(lines[i]))) appc.text(lines[i], 190, 96 + ((i- sp) * 20)); // print selected Subject's lines (if not a number line)
                    }
                    if (PApplet.parseInt(lines[i]) >= SelectedSubject) subject = PApplet.parseInt(lines[i]);
                }
            }
            catch (Exception e)
            {
                println("The Help file is missing or corrupted!");
                appc.text("The Help file is missing!", 90, 150);
            }
        }
        else HelpLoopNum = 1; // 1 = next loop will be marked as 2nd
        MouseJustPressed = false;
        appc.noLoop();
    }

    public void HelpWindow_close(GWindow HelpWindow)
    {
        HelpWindow = null;
        SelectedSubject *= -1; // so 2nd help window can't be opened & selected subject remembered
        HelpLoopNum = 0;
    }

//=======================================================================================================

    public void SaveSettingsFile()
    {
        String[] data = new String[6]; // total number of lines to be written
        data[0] = "Don't change the order of these entries:";
        data[1] = "Magnify     =\t" + Magnify;
        data[2] = "EditSize    =\t" + EditSize;
        data[3] = "EditRedraw  =\t" + EditRedraw;
        data[4] = "ProgStep    =\t" + ProgStep;
        data[5] = "KeypadPos  =\t" + KeypadPos;
        saveStrings("data/Settings.awg", data);
        println("Writing to Settings file...");
        println(data[1]);
        println(data[2]);
        println(data[3]);
        println(data[4]);
        println(data[5]);
    }

    public void handleTextEvents(GEditableTextControl textControl, GEvent event)
    {
        try
        {
            if (txf1.getText() != null) UserInput = trim(txf1.getText());
            if (txf2.getText() != null)
            {
                if (Float.isNaN(PApplet.parseFloat(trim(txf2.getText()))));
                else ProgStep = trim(txf2.getText());
            }
        }
        catch (Exception e)
        {
            // println("Unknown error!");
//    e.printStackTrace();
        }
    }

    public void mousePressed()
    {
        MouseJustPressed = true;
        if (Drawing && OptionsMag == 0) // if drawing & options window is closed
        {
            if (ProgDraw && mouseX < (300 * Magnify) && (Wave0Filled || MinX > 20) && MinX < (300 * Magnify) + 19) MinX += min((300 * Magnify) - 1, PApplet.parseInt(ProgStep)); // if progressive draw, move to the right by progressive step size
        }
        loop();
    }

    public void mouseReleased()
    {
        if (Drawing && OptionsMag == 0) // if drawing, & if options window is closed
        {
            if (Autofill == 1 && millis() > TouchTime + 500) DrawWave = 1; // if drawing & Autofill on
        }
        if (Autofill == 2 && DrawWave == 2 && EditRedraw == 1 && mouseX >= 20 && mouseX <= (Magnify * 300) + 20 && mouseY > 57 && mouseY < (175 * Magnify) + 57) DrawWave = 1; // if erasing when filled & drawing area just been touched
        if (Mode == 2)
        {
            Mode = 1;
            Delimiter = null;
        }
    }

    public void keyPressed()
    {
        loop();
    }

    public void serialEvent(Serial SerialData)
    {
        loop();
    }

    public void ReadSerialData(boolean SendingData) // and UPLOAD ARBITRARY WAVE
    {
        while (SendingData && SerialData.available() == 0 &&  millis() < TouchTime + 500) {delay(150);} // wait up to 500 milliseconds to receive answer from Arduino
        if (SendingData && SerialData.available() == 0)
        {
            Connected = false; // lost connection
            fill(40);
            noStroke();
            rect((300 * Magnify) + 215, 10, 175, 15);
            fill(255);
            text("Can't find Arduino!", (300 * Magnify) + 240, 22);
            Info = "               Lost connection with Arduino!";
        }
        while (SerialData.available() > 0)
        {
            String data = SerialData.readStringUntil('\n');
            try
            {
                if (data.length() > 2)
                {
                    //       println("Delimiter is: " + Delimiter);
                    //       println("SendControl is: " + SendControl);
                    if (Delimiter == null)
                    {
                        String cutData = data.substring(0, 8);
                        if (cutData.equals("INFO>   ")) Info = "   " + data.substring(8);
                    }
                    else Info = null;
                    if (Delimiter.equals("G")) // (GUI) sent to Arduino at start-up
                    {
                        println(data);
                        String cutData = data.substring(0, 9);
                        if (cutData.equals("Hello GUI"))
                        {
                            println("   ... Hello Arduino!\n\n");
                            Delimiter = null;
                        }
                    }
                    //       println("data is: " + data);
                    if (Delimiter.equals("a")) // upload arbitrary wave:
                    {
                        //        println("data is: " + data);
                        while (SerialData.available() > 0)
                        {
                            data = SerialData.readStringUntil('\n');
                            //          println(data);
                        }
                        for(int i = 0; i < ArbitraryPointNumber; i++)
                        {
                            //         println("i = " + i + "  ArbitraryWave[i] = " + ArbitraryWave[i]);
                            if (ArbitraryWaveStep[i] > -1) SerialData.write(Integer.toString(ArbitraryWaveStep[i]) + '-'); //send
                            SerialData.write(Integer.toString(ArbitraryWave[i])); //send
                            if (i <= ArbitraryPointNumber - 2) SerialData.write(','); //send
                            else SerialData.write(';'); //send
                            String[] partInput;
                            if (SquareWaveSync) partInput = split(AnalogActualFreq, " "); // split at space
                            else        partInput = split(SqWaveActualFreq, " "); // split at space
                            int del = 50;
                            if (PApplet.parseFloat(partInput[0]) < 500 && partInput[1].equals("mHz")) del = 50;
                            if ( i % 20 == 0) delay(del);
                        }
                        noStroke();
                        fill(40);
                        rect((Magnify * 300) - 42, 7, 86, 20);
                        fill(255);
                        textSize(15);
                        text("UPLOAD", (Magnify * 300) - 35, 22);
                        WaveShape = 2;
                        String cutDuty = AnalogActualDuty.substring(0, 4); // remove % symbol
                        if (PApplet.parseFloat(cutDuty) > 0)
                        {
                            Info = "     Set duty cycle to 0% to avoid mirrored effect.";
                            InfoTime = millis() + 300;
                        }
                        break;
                    }
                    else if (Delimiter.equals("w"))
                    {
                        String cutData = data.substring(data.length() - 23);
                        if      (cutData.equals("** Sine Wave *********\n")) WaveShape = 0;
                        else if (cutData.equals(" Triangle Wave *******\n")) WaveShape = 1;
                        else if (cutData.equals("Arbitrary Wave *******\n")) WaveShape = 2;
                        //       println("w cutData = " + cutData);
                        //       println("WaveShape = " + WaveShape);
                    }
                    else if (Delimiter.equals("e"))
                    {
                        String cutData = data.substring(3, 20);
                        if      (cutData.equals("Exact Mode is OFF")) ExactFreqMode = false;
                        else if (cutData.equals("Exact Mode is ON ")) ExactFreqMode = true;
                        //       println("ExactFreqMode is: " + ExactFreqMode);
                    }
                    else if (Delimiter.equals("v"))
                    {
                        String cutData = data.substring(3, 19);
                        if      (cutData.equals("Waves are Unsync")) SquareWaveSync = false;
                        else if (cutData.equals("Waves are Synchr")) SquareWaveSync = true;
                        //       println("Sq Wave Sync is: " + SquareWaveSync);
                    }
                    else if (Delimiter.equals("R"))
                    {
                        String cutData = data.substring(3, 16);
                        if      (cutData.equals("Timer Running")) TimerRun = true;
                        //       println("R cutData = " + cutData);
                    }
                    else if (Delimiter.equals("r"))
                    {
                        String cutData = data.substring(3, 14);
                        if      (cutData.equals("Timer Reset")) TimerRun = false;
                        //       println("r cutData = " + cutData);
                    }
                    else if (Delimiter.equals("U"))
                    {
                        String cutData = data.substring(3, 10);
                        if      (cutData.equals("Time Up")) TimeUp = 1;
                        //       println("U cutData = " + cutData);
                    }
                    //      println("data is: " + data);
                    if (!Delimiter.equals("G"))
                    {
                        String part = trim(data);
                        String cutData = part.substring(0, 16);
                        //         println("cutData = " + cutData);
                        String[] partData = splitTokens(data, ":,");
                        if      (partData[0].equals("   Analogue Wave Freq")
                                || partData[0].equals(">> Analogue Wave Freq"))   AnalogActualFreq = trim(partData[1]);
                        else if (partData[0].equals("   Analogue Wave Period")) // {AnalogActualPeriod = trim(partData[1]); AnalogActualPulseW = trim(partData[3]);}
                        {
                            String part1 = trim(partData[1]);
                            String end1 = part1.substring(part1.length()-2);
                            if (end1.equals("uS")) AnalogActualPeriod = part1.substring(0, part1.length() - 2) + "µS"; // change "uS" to "µS"
                            else                   AnalogActualPeriod = part1;
                            String part3 = trim(partData[3]);
                            String end3 = part3.substring(part3.length()-2);
                            if (end3.equals("uS")) AnalogActualPulseW = part3.substring(0, part3.length() - 2) + "µS"; // change "uS" to "µS"
                            else                   AnalogActualPulseW = part3;
                        }
                        else if (partData[0].equals("   Analogue Wave Duty-cycle")) AnalogActualDuty = trim(partData[1]);
                        else if (partData[0].equals("   Unsync'ed Sq.Wave Freq")
                                || partData[0].equals(">> Unsync'ed Sq.Wave Freq"))   SqWaveActualFreq = trim(partData[1]);
                        else if (partData[0].equals("   Unsync'ed Sq.Wave Period"))
                        {
                            String part1 = trim(partData[1]);
                            String end1 = part1.substring(part1.length()-2);
                            if (end1.equals("uS")) SqWaveActualPeriod = part1.substring(0, part1.length() - 2) + "µS"; // change "uS" to "µS"
                            else SqWaveActualPeriod = trim(partData[1]);
                            String part3 = trim(partData[3]);
                            String end3 = part3.substring(part3.length()-2);
                            if (end3.equals("uS")) SqWaveActualPulseW = part3.substring(0, part3.length() - 2) + "µS"; // change "uS" to "µS"
                            else SqWaveActualPulseW = part3;
                        }
                        else if (partData[0].equals("   Unsync'ed Sq.Wave Duty-cycle")) SqWaveActualDuty   = trim(partData[1]);
                        else if (cutData.equals("Exact Mode is OF")) ExactFreqMode = false;
                        else if (cutData.equals("Exact Mode is ON")) ExactFreqMode = true;
                        else if (cutData.equals("Waves are Unsync")) SquareWaveSync = false;
                        else if (cutData.equals("Waves are Synchr")) SquareWaveSync = true;
                        else if (cutData.equals("********** Sine ")) WaveShape = 0;
                        else if (cutData.equals("******** Triangl")) WaveShape = 1;
                        else if (cutData.equals("******* Arbitrar")) WaveShape = 2;
                        //        else println("Found no match ");
                    }
                }
            }
            catch (Exception e)
            {
                // println("Unknown error!");
                //     e.printStackTrace();
            }
        } // end of while (SerialData.available() > 0)
        if (!Delimiter.equals("G")) Delimiter = null;
    }

           //                 column, row,  width, height, shift(logo to the left by "s", & up 3)
           //                   v      v      v      v      v
    public void DrawButtons(int c, int r, int w, int h, int s) // Draw row of 10 buttons (keypad) at bottom (and quantifier buttons)
    {
        int sp = c; // start position
        r = r + max(350, (175 * KeypadPos)); // top of Row of bottons
        noStroke();
        for (c = sp + (Magnify * 300); c < 355 + (Magnify * 300); c += (w + 5)) // c = vertical column (of pixels)
        {
            fill(120);
            rect(c, r, 4 + w, h + 4); // bottom & right of button
            stroke(210);
            line(c, r, c + 3 + w, r);              // top of button
            line(c + 1, r + 1, c + 2 + w, r + 1);  // top of button
            line(c, r, c, r + h + 2);                 // left side of button
            line(c + 1, r + 1, c + 1, r + h + 1);     // left side of button
            noStroke();
            fill(170, 170, 170);
            rect(c + 2, r + 2, w, h); // centre of button
            int i = ((c - 31 - (Magnify * 300)) / 31); // calculate which logo to print on button if keypad pressed
            String logo = str(i);
            if (i > 9) logo = ".";
            if (sp > 143) // if quantifier buttons pressed
            {
                textSize(15);
                if (Symbol < 2) logo = Quantifier1.get(min(5, ((c - ((Magnify * 300) + 264)) / 30) + (2 * Symbol))); // calculate which logo to print on button
                else logo = "";
                //   println("logo = " + logo);
            }
            fill(130);
            text(logo, c + 8 - min(3, s), r + 20 - s);
            fill(255);
            text(logo, c + 9 - min(3, s), r + 21 - s);
            textSize(18);
        }
    }

    // Draw <<<<<<<< >>>>>>>> Button (p = 0 for unpressed,  p = 1 for pressed):
    public void DrawDownUpButton(int p)
    {
        int c = 43 + (Magnify * 300);
        int r = 49 + max(350, (175 * KeypadPos)); // top of Row of botton
        fill(120 + (p * 90));
        rect(c, r, 340, 30); // bottom & right of button
        stroke(210 - (p * 140));
        line(c, r, c + 3 + 336, r);              // top of button
        line(c + 1, r + 1, c + 2 + 336, r + 1);  // top of button
        line(c, r, c, r + 26 + 2);                 // left side of button
        line(c + 1, r + 1, c + 1, r + 26 + 1);     // left side of button
        noStroke();
        fill(170);
        rect(c + 2, r + 2, 336, 26); // centre of button
        Mode = p + 1; // button drawn unpressed (1) / pressed (2)
        fill(130);
        text("<<<<<<<< >>>>>>>>", c + 53 + p, r + 19 + p);
        fill(255);
        text("<<<<<<<< >>>>>>>>", c + 54 + p, r + 20 + p);
    }

    //                    column, row,  width, height, shift(logo to the left by "s", & up 3)
//                      v      v      v      v      v
    public void ButtonPressed(int cl, int r, int w, int h, int s) // if BUTTON PRESSED:
    {
        int c;
        int i = ((mouseX - 42 - (Magnify * 300)) / 31); // calculate logo to print on button
        String logo = str(i);
        if (i > 9) logo = ".";
        if (cl > 143) c = cl + (Magnify * 300) + ((w + 5) * ((mouseX - ((Magnify * 300) + 264)) / 30)); // if quantifier buttons pressed, calculate vertical column (of pixels) at left of pressed button
        else c = (i * 31) + 43 + (Magnify * 300); // if keypad pressed, calculate vertical column (of pixels) at left of pressed button
        r = r + max(350, (175 * KeypadPos)); // top of button
        noStroke();
        fill(55);
        rect(c, r, 4 + w, h + 4); // top & left of button
        stroke(210);
        line(c + 1, r + h + 3, c + 3 + w, r + h + 3);  // bottom of button
        line(c + 2, r + h + 2, c + 2 + w, r + h + 2); // bottom of button
        line(c + w + 3, r + 1, c + w + 3, r + h + 2); // right side of button
        line(c + w + 2, r + 2, c + w + 2, r + h + 1); // right side of button
        noStroke();
        fill(170);
        rect(c + 2, r + 2, w, h); // centre of button
        if (cl > 143) // if quantifier buttons pressed
        {
            textSize(15);
            if (Symbol < 2) logo = Quantifier1.get(min(5, ((c - ((Magnify * 300) + 264)) / 30) + (2 * Symbol))); // calculate logo to print on button
            else logo = "";
            //   println("logo = " + logo);
        }
        else if (MouseJustPressed) // if keypad just pressed
        {
            UserInput = UserInput + logo;
            txf1.setText(UserInput);
        }
        fill(130);
        text(logo, c + 9 - min(3, s), r + 21 - s);
        fill(255);
        text(logo, c + 10 - min(3, s), r + 22 - s);
        textSize(18);
    }

    public void QuantifyInput(int moveDecPoint)
    {
        if (PApplet.parseFloat(UserInput) > 0)
        {
            while (UserInput.charAt(0) == '0' && PApplet.parseFloat(UserInput.substring(1, 2)) >= 0) {UserInput = UserInput.substring(1);} // remove any extra zeros from front
            if (UserInput.charAt(0) == '.') UserInput = "0" + UserInput; // if starts with "." add "0" in front
            if (UserInput.substring(UserInput.length() - 1).equals(".")) UserInput = UserInput + "0"; // if ends with "." add "0" on the end
            txf1.setText(UserInput);
            String[] partInput = split(UserInput, "."); // split UserInput at decimal point
            if (partInput[0].length() <= 3 || (Quantifier.equals("  ") && Descriptor.equals("S"))) // if 1st part of UserInput is not too long, or if descriptor is seconds
            {
                String noDecPoint = UserInput;
                if (UserInput.length() - partInput[0].length() > 1) // if decimal fraction exists:
                {
                    noDecPoint = partInput[0] + partInput[1]; // user input with decimal point removed
                }
                int decPlaces = UserInput.length();
                for(int i = 0; i < decPlaces; i++) { if (UserInput.charAt(i) == '.') decPlaces = i; } // count number of places before any decimal point
                if (PApplet.parseInt(moveDecPoint) >= 0) // if need to move decimal point to the right (or don't move it)
                {
                    if (PApplet.parseInt(noDecPoint.length()) <= moveDecPoint + decPlaces) // if user input is too short
                    {
                        for(int i = PApplet.parseInt(noDecPoint.length()); i <= moveDecPoint + decPlaces; i++) { noDecPoint += "0"; } // add zeros to end if necessary before inserting decimal point
                    }
                    String cutData1 = noDecPoint.substring(0, moveDecPoint + decPlaces); // cut where decimal point needs to be inserted (maybe didn't need zeros added)
                    String cutData2 = noDecPoint.substring(moveDecPoint + decPlaces); // cut where decimal point needs to be inserted    (might not be a zero)
                    QuantifiedInput = cutData1 + "." + cutData2; //  insert decimal point
                    //     println("QuantifiedInput = " + QuantifiedInput);
                    return;
                }
                else // if need to move decimal point to the left (WILL need to add zeros)
                {
                    if (PApplet.parseInt(noDecPoint.length()) >= moveDecPoint + decPlaces) // ensure user input is too short (will be, as any excess zeros on the front already removed)
                    {
                        for(int i = 0; i >= moveDecPoint + decPlaces; i--) // count zeros being added to the front
                        {
                            if (i > moveDecPoint + decPlaces) noDecPoint = "0" + noDecPoint; // add zero to the front
                            else noDecPoint = "0." + noDecPoint;                            // add zero plus decimal point if it's the last zero to add
                        }
                        QuantifiedInput = noDecPoint;
                        //     println("QuantifiedInput = " + QuantifiedInput);
                        return;
                    }
                }
            }
            else Info = "           Maximum input is 999.99999";
        }
        else if (SweepStep < 4) Info = "             That input is invalid.";
        InfoTime = millis() + 300;
        SendControl = "";
        Delimiter = null;
    }

    public void ReadActualSettings()
    {
        String[] partInput;
        for(int i = 0; i < 2; i++)
        {
            if (SettingTouched[i] == 1)
            {
                if (i == 0) partInput = split(AnalogActualFreq, " "); // split at space
                else        partInput = split(SqWaveActualFreq, " "); // split at space
                if      (partInput[1].equals("mHz")) InputMultiplier = -1;
                else if (partInput[1].equals("Hz"))  InputMultiplier = 0;
                else if (partInput[1].equals("kHz")) InputMultiplier = 1;
                else if (partInput[1].equals("MHz")) InputMultiplier = 2;
                SlideSetting[i] = PApplet.parseFloat(partInput[0]) * pow(1000, (InputMultiplier));
//      println("new[" + i + "] = " + SlideSetting[i]);
            }
            else if (SettingTouched[i] == 2)
            {
                if (i == 0) partInput = split(AnalogActualPeriod, " "); // split at space
                else        partInput = split(SqWaveActualPeriod, " "); // split at space
                if      (partInput[1].equals("nS")) InputMultiplier = -2;
                else if (partInput[1].equals("µS"))  InputMultiplier = -1;
                else if (partInput[1].equals("mS")) InputMultiplier = 0;
                else if (partInput[1].equals("Sec")) InputMultiplier = 1;
                SlideSetting[i] = PApplet.parseFloat(partInput[0]) * pow(1000, (InputMultiplier));
//      println("new[" + i + "] = " + SlideSetting[i]);
            }
            else if (SettingTouched[i] == 3)
            {
                if (i == 0) partInput = split(AnalogActualDuty, " "); // split at space
                else        partInput = split(SqWaveActualDuty, " "); // split at space
                SlideSetting[i] = PApplet.parseFloat(partInput[0]);
//      println("new[" + i + "] = " + SlideSetting[i]);
            }
            else if (SettingTouched[i] == 4)
            {
                if (i == 0) partInput = split(AnalogActualPulseW, " "); // split at space
                else        partInput = split(SqWaveActualPulseW, " "); // split at space
                switch (partInput[1]) {
                    case "nS" -> InputMultiplier = -1;
                    case "µS" -> InputMultiplier = 0;
                    case "mS" -> InputMultiplier = 1;
                    case "Sec" -> InputMultiplier = 2;
                }
                SlideSetting[i] = PApplet.parseFloat(partInput[0]) * pow(1000, (InputMultiplier));
//      println("new[" + i + "] = " + SlideSetting[i]);
            }
        } // end of for loop
    }

    public void draw()
    {
        if (TimerRun) // if timer running
        {
            if (mousePressed) TimerTouchTime = millis();
            else if (millis() > TimerTouchTime + 200)
            {
                int newSecs = ((millis() / 1000) - StartTime) % 60;
                delay(100);
                if (newSecs != TimerSecs)
                {
                    if (TimerSecs == 59)
                    {
                        if (TimerMins < 59) TimerMins++;
                        else
                        {
                            TimerMins = 0;
                            if (TimerHours < 23) TimerHours++;
                            else
                            {
                                StartTime = millis() / 1000; // reset time reference to prevent double rollover after ~ 49 days
                                TimerHours = 0;
                                TimerDays++;
                            }
                        }
                    }
                    TimerSecs = newSecs;
                    if (PeriodD + PeriodH + PeriodM + PeriodS > 0) // if not set to zero
                    {
                        boolean elapsed = false;
                        if (TimerDays != PeriodD)
                        {
                            if (TimerDays > PeriodD) elapsed = true;
                        }
                        else if (TimerHours != PeriodH)
                        {
                            if (TimerHours > PeriodH) elapsed = true;
                        }
                        else if (TimerMins != PeriodM)
                        {
                            if (TimerMins > PeriodM) elapsed = true;
                        }
                        else if (TimerSecs >= PeriodS) elapsed = true;
                        if (elapsed) // if set time has elapsed
                        {
                            for (int i = 0; Connected && TimeUp == 0 && i < 2; i++) // up to 2 attempts to get a response from Arduino
                            {
                                if (TimeUp == 0) Delimiter = "U";
                                SerialData.write(Delimiter); // send TimeUp signal to Arduino.
                                println("SENT: " + Delimiter);
                                TouchTime = millis() - 200; // wait up to 300mS instead of 500mS for response from Arduino
                                ReadSerialData(true);
                                //           println("After sending: TimeUp = " + TimeUp);
                            }
                            fill(80, 50, 50); // dim red
                            rect((300 * Magnify) + 224, 222, 136, 16); // max(330, (175 * Magnify) - 20)); // Right settings area
                            if (TimeUp < 2) // announce Time is Up anyway (even if no response from Arduino)
                            {
                                TimeUp = 2;
                                fill(0);
                                text("***                   ***", (300 * Magnify) + 226, 237); // shadow
                                text("TIME IS UP!", (300 * Magnify) + 253, 237); // shadow
                                fill(255, 255, 0);
                                text("***                   ***", (300 * Magnify) + 225, 236);
                                text("TIME IS UP!", (300 * Magnify) + 252, 236);
                            }
                            else
                            {
                                TimeUp = 1;
                                fill(0);
                                text("TIME IS UP!", (300 * Magnify) + 253, 237); // shadow
                                fill(255, 255, 0);
                                text("TIME IS UP!", (300 * Magnify) + 252, 236);
                            }
                            //          println("Time is Up = " + TimeUp);
                        }
                        fill(80, 50, 50); // dim red
                        rect((300 * Magnify) + 224, 248, 148, 16); // max(330, (175 * KeypadPos) - 20)); // Right settings area
                        rect((300 * Magnify) + 224, 272, 20, 61); // max(330, (175 * KeypadPos) - 20)); // Right settings area
                        fill(255);
                        text(TimerDays + " Days", (300 * Magnify) + 225, 260);
                        text(nf(TimerHours, 2, 0), (300 * Magnify) + 225, 284);
                        text(nf(TimerMins, 2, 0), (300 * Magnify) + 225, 308);
                        fill(255);
                        text(nf(TimerSecs, 2, 0), (300 * Magnify) + 225, 332);
                    }
                }
            }
        }
        if (!TimerRun || millis() < TimerTouchTime + 200)
        {
            if (Mode == 2) // && millis() >= TouchTime)
            {
                int updateTime = 100; // set next UpdateTime
                while (millis() < TouchTime + updateTime) {delay(1);} // wait until update time
                TouchTime = millis();
                boolean send = true;
                if (SettingTouched[0] == 0 || SettingTouched[1] == 0) send = false; // for proportional operation || SettingTouched[0] == SettingTouched[1]) send = false;
                int x = (mouseX - ((Magnify * 300) + 53)); // left side limit of slide button (<<<<< >>>>> button)
                float multiplier = 0;
                if (x <= 139) multiplier = fscale(25, 140, 0, 159, max(25, PApplet.parseFloat(x)), -0.6f); // adjust linearity of touch read x
                else if (x <= 159) multiplier = fscale(140, 160, 159, 160, PApplet.parseFloat(x), -0.5f); // adjust linearity of touch read x
                else if (x >= 160) multiplier = fscale(160, 299, 160, 319, min(299, PApplet.parseFloat(x)), 0.4f); // adjust linearity of touch read x
                if ((multiplier - 160) >= 0) multiplier = 1 + ((multiplier - 160) / 2000);
                else                   multiplier = 1 / (1 + (-(multiplier - 160) / 2000));
                int sp = 0; // start place
                if (TimerMode == 0 && SettingTouched[0] == 0) sp = 1; //  starting place for the for loop for proportional operation || SettingTouched[0] == SettingTouched[1]) sp = 1; // starting place for the for loop
                int ep = min(1, SettingTouched[1]); // end place
                if (TimerMode > 0) ep = 0;
                for(int i = sp; i <= ep; i++)
                {
                    if (SettingTouched[i] == 3)
                    {
                        SlideSetting[i] = constrain(fscale(21, 300, 0, 100, PApplet.parseFloat(x), 0.0f), 0, 100);
                        Delimiter = "d";
                    }
                    else if (SettingTouched[i] >= 0)
                    {
                        SlideSetting[i] = SlideSetting[i] * multiplier;
                        if (SettingTouched[i] == 1) Delimiter = "h";
                        if (SettingTouched[i] == 2) Delimiter = "m";
                        if (SettingTouched[i] == 4) Delimiter = "u";
                    }
                    if (send)
                    {
                        if (i == 0) SendControl = "b ";
                        else        SendControl = "b  ";
                    }
                    if (SlideSetting[i] >= 10000000) // if value is 10 million or more...
                    {
                        SerialData.write(SendControl + nf(SlideSetting[i], 8, 1) + Delimiter); // send signal to Arduino. "nf(..." prevents scientific notation being sent to Arduino
//          println(" DATA SENT: " + SendControl + nf(SlideSetting[i], 8, 1) + Delimiter);
                    }
                    else if (SlideSetting[i] < 0.001f) // if value is less than 1 thousandth...
                    {
                        SerialData.write(SendControl + nf(SlideSetting[i], 1, 8) + Delimiter); // send signal to Arduino. "nf(..." prevents scientific notation being sent to Arduino
//          println(" DATA SENT: " + SendControl + nf(SlideSetting[i], 1, 8) + Delimiter);
                    }
                    else SerialData.write(SendControl + SlideSetting[i] + Delimiter); // send signal to Arduino
//        println(" DATA SENT: " + SendControl + SlideSetting[i] + Delimiter);
                } // end of for loop
                delay(updateTime - 30); // rest CPU, but allow 30 mS to read serial data & update display
                if (Connected && SerialData.available() > 0) ReadSerialData(false); // if no serial waiting, forget it this time
                noStroke();
                fill(50, 50, 80); // dim blue
                for(int i = 115; i < 319; i = i + 63)
                {
                    rect((300 * Magnify) + 53, i, 152, 20); // max(330, (175 * KeypadPos) - 20)); // Left settings area patches
                }
                fill(255);
                textSize(18);
                text(AnalogActualFreq, (300 * Magnify) + 57, 130);
                text(AnalogActualPeriod, (300 * Magnify) + 57, 193);
                text("Actual " + AnalogActualDuty, (300 * Magnify) + 57, 256);
                text(AnalogActualPulseW, (300 * Magnify) + 57, 319);
                if (TimerMode == 0)
                {
                    fill(80, 50, 50); // dim red
                    for(int i = 115; i < 319; i = i + 63)
                    {
                        rect((300 * Magnify) + 221, i, 152, 20); // max(330, (175 * KeypadPos) - 20)); // Right settings area patches
                    }
                    if (SquareWaveSync) fill(175);
                    else fill(255);
                    text(SqWaveActualFreq, (300 * Magnify) + 225, 130);
                    text(SqWaveActualPeriod, (300 * Magnify) + 225, 193);
                    text("Actual " + SqWaveActualDuty, (300 * Magnify) + 225, 256);
                    text(SqWaveActualPulseW, (300 * Magnify) + 225, 319);
                }
            }
            else if (SweepStep > 5) // if sweep runnng
            {
                int ix = 0;
                if (MouseJustPressed && mouseY > max(440, (175 * KeypadPos) + 90) && mouseY < max(455, (175 * KeypadPos) + 105)) // if row under control area: in FREQ SWEEP Mode =================================================
                {
                    noStroke();
                    fill(40);
                    textSize(15);
                    if (mouseX > (Magnify * 300)  + 60 && mouseX < (Magnify * 300) + 102) // if STOP
                    {
                        MouseJustPressed = false; // debounce
                        rect((Magnify * 300) + 58, max(440, (175 * KeypadPos) + 90), 47, 18);
                        fill(255);
                        if (SweepStep > 5)
                        {
                            SweepStep = SweepStep - 5;
                            SerialData.write("s"); // send signal to Arduino
                            println("SENT: s"); // send signal to Arduino
                            fill(0, 255, 0);
                            text("GO", (Magnify * 300) + 70, max(454, (175 * KeypadPos) + 104));
                            ix = 4; // 2 extra iterations in for loop below
                            delay(16); // extra time to receive serial data for returning to target freq (set before sweep)
                        }
                    }
                }
                if (Connected && SerialData.available() > 0)
                {
                    delay(4); // allow time for serial buffer to fill up to delimiter 'z' (for both waves)
                    try
                    {
                        for(int i = -ix; i < 2 * (min(1, SettingTouched[0]) + min(1, SettingTouched[1])); i++) // up to 2 iterations  (or 4 if both waves selected)
                        {
                            String data = SerialData.readStringUntil('z'); // 10 = linefeed
                            //          println("data[" + i + "]: " + data);
                            if (data != null && data.length() > 4)
                            {
                                String cutData = data.substring(0, 4);
                                if (cutData.equals("AAF "))
                                {
                                    AnalogActualFreq = data.substring(4);
                                }
                                else if (cutData.equals("SAF "))
                                {
                                    SqWaveActualFreq = data.substring(4);
                                }
                            }
                            else
                            {
                                //            println("null");
                                break;
                            }
                            if (SerialData.available() == 0) break;
                        }
                    }
                    catch (Exception e)
                    {
                        println("Error");
                    }
                }
                //     println(" A loop millis() - TouchTime = " + (millis() - TouchTime));
                noStroke(); //
                fill(50, 50, 80); // dim blue
                rect((300 * Magnify) + 53, 115, 152, 20); // max(330, (175 * KeypadPos) - 20)); // Left freq setting patch
                fill(80, 50, 50); // dim red
                rect((300 * Magnify) + 221, 115, 152, 20); // max(330, (175 * KeypadPos) - 20)); // Right freq setting patch
                fill(255);
                textSize(18);
                text(AnalogActualFreq, (300 * Magnify) + 57, 130);
                if (SquareWaveSync) fill(175);
                text(SqWaveActualFreq, (300 * Magnify) + 225, 130);
            }
            else if (OptionsMag == 0) // || (Mode == 2 && millis() < TouchTime + 500)) // if Options window not open. (OptionsMag stores the value of Magnify when the Options window is open) ==================================
            {
                if (Mode == 2 && SerialData.available() > 0) ReadSerialData(false);
                noFill();
                stroke(20); //
                rect((300 * Magnify) + 41, 36, 344, max(395, (175 * KeypadPos) + 45)); // control area border bottom right
                stroke(120); //
                rect((300 * Magnify) + 40, 35, 344, max(395, (175 * KeypadPos) + 45)); // control area border top left
                noStroke(); //
                fill(40);
                rect((300 * Magnify) + 386, 56, 14, max(365, (175 * KeypadPos) + 15)); // right of control area, to cover over-spill if too many digits entered
                fill(70);
                rect((300 * Magnify) + 41, 36, 344, max(395, (175 * KeypadPos) + 45)); // control area
                fill(170);
                rect((300 * Magnify) + 122, max(366, (175 * KeypadPos) + 15) + 2, 92, 27); // textfield border top left
                fill(200);
                rect((300 * Magnify) + 123, max(366, (175 * KeypadPos) + 15) + 3, 91, 26); // textfield border bottom right
                noFill();
                stroke(120); //
                rect((300 * Magnify) + 53, 94, 152, 248); // max(330, (175 * KeypadPos) - 20)); // Left settings area border bottom right
                rect((300 * Magnify) + 221, 94, 152, 248); // max(330, (175 * KeypadPos) - 20)); // Left settings area border bottom right
                stroke(40); //
                rect((300 * Magnify) + 52, 93, 152, 248); // max(330, (175 * KeypadPos) - 20)); // Right settings area border top left
                rect((300 * Magnify) + 220, 93, 152, 248); // max(330, (175 * KeypadPos) - 20)); // Right settings area border top left
                noStroke(); //
                fill(50, 50, 80); // dim blue
                rect((300 * Magnify) + 53, 94, 152, 248); // max(330, (175 * KeypadPos) - 20)); // Left settings area
                fill(80, 50, 50); // dim red
                rect((300 * Magnify) + 221, 94, 152, 248); // max(330, (175 * KeypadPos) - 20)); // Right settings area

                fill(255);
                if (Connected && Delimiter != null && Mode < 2)
                {
                    SerialData.write(SendControl + QuantifiedInput + Delimiter); // send signal to Arduino
//        if (Delimiter != "G") println("DATA SENT: " + SendControl + QuantifiedInput + Delimiter);
                    MouseJustPressed = false; // debounce
                    if (Delimiter.equals("w")) TouchTime = millis() + 1500; // wait extra time for answer from Arduino (if changing waveshape)
                    else                  TouchTime = millis();       // wait usual time (up to 500mS) for answer from Arduino
                    ReadSerialData(true);
                }
                else if (Mode < 2)
                {
                    if (Connected && SerialData.available() > 0) ReadSerialData(false);
                    if (!Connected && Delimiter != null)
                    {
                        Delimiter = null;
                        Info = "                   No connection to Arduino!";
                        delay(150);
                        InfoTime = millis();
                    }
                    if (Info != null)
                    {
                        fill(255, 140, 0);
                        textSize(13);
                        text(Info, (300 * Magnify) + 44, max(360, (175 * KeypadPos) + 10));
                        fill(255);
                    }
                    if ((Mode == 0 && millis() > InfoTime + 500) || (Mode > 1 && (SettingTouched[0] > 0 || SettingTouched[1] > 0))) Info = null;
                }
                textSize(20);
                if (MouseJustPressed && mouseX > (Magnify * 300) + 86 && mouseX < (Magnify * 300) + 173 && mouseY > 40 && mouseY < 60) // if we clicked Waveshape
                {
                    SendControl = "";
                    QuantifiedInput = "";
                    Delimiter = "w";
                    fill(0);
                }
                if (WaveShape == 0) text("Sinewave", (300 * Magnify) + 86, 60);
                else if (WaveShape == 1) text("Triangle", (300 * Magnify) + 90, 60);
                else if (WaveShape == 2) text("Arbitrary", (300 * Magnify) + 86, 60);
                fill(255);
                if (TimerMode == 0) text("Square Wave", (300 * Magnify) + 235, 60);
                else                text("   TIMER", (300 * Magnify) + 250, 61);
                textSize(18);
                if (MouseJustPressed && mouseX > (Magnify * 300) + 60 && mouseX < (Magnify * 300) + 200 && mouseY > 70 && mouseY < 87) // if we clicked Exact Mode
                {
                    SendControl = "";
                    QuantifiedInput = "";
                    Delimiter = "e";
                    MouseJustPressed = false; // debounce
                    fill(0);
                }
                if (ExactFreqMode) text("Exact Mode ON", (300 * Magnify) + 60, 85);
                else               text("Exact Mode OFF", (300 * Magnify) + 60, 85);
                fill(255);
                if (MouseJustPressed && mouseX > (Magnify * 300) + 256 && mouseX < (Magnify * 300) + 338 && mouseY > 70 && mouseY < 87) // if we clicked Sync ON / OFF. or if Timer: Pos/Neg Output
                {
                    MouseJustPressed = false; // debounce
                    SendControl = "";
                    QuantifiedInput = "";
                    if (TimerMode == 0) Delimiter = "v";
                    else if (Connected)
                    {
                        if (TimerInvert)
                        {
                            Delimiter = "i";
                            TimerInvert = false;
                        }
                        else
                        {
                            Delimiter = "I";
                            TimerInvert = true;
                        }
                    }
                    else Delimiter = "i";
                    fill(0);
                }
                if (TimerMode == 0)
                {
                    if (SquareWaveSync) text("Sync ON", (300 * Magnify) + 258, 85);
                    else               text("Sync OFF", (300 * Magnify) + 258, 85);
                }
                else
                {
                    if (TimerInvert) text("Neg Output", (300 * Magnify) + 246, 85);
                    else             text("Pos Output", (300 * Magnify) + 248, 85);
                }
                fill(255);
                if (MouseJustPressed && mouseX > (Magnify * 300) + 53 && mouseX < (Magnify * 300) + 373 - (170 * TimerMode) && mouseY > 98 && mouseY < 148) // if we clicked Freq
                {
                    MouseJustPressed = false; // debounce
                    int div =  (mouseX - (Magnify * 300)) / 212; // will be 0 or 1
                    int control = 1 - div; // opposite to div (to match Arduino's Control)
                    int div1 = div;
                    if (mouseX > (Magnify * 300) + 203 && mouseX < (Magnify * 300) + 222) control = 2; // if area between waves touched
                    if (control == 2) div1 = 1 - div; // if area between waves touched, div1 = opposite wave to div
                    if (SettingTouched[div] == 1 && SettingTouched[div1] == 1 && Descriptor.equals("Hz") && Mode == 0 && SweepStep == 0) // if wave or waves already selected & mode 0 & sweep off
                    {
                        Delimiter = "h";
                        QuantifyInput((InputMultiplier - 1) * 3);
                        if (control > 0)
                        {
                            if (TimerMode == 0) SendControl = "b "; // switch Control to analog ("b " already sent if in timer mode)
                            AnalogTargetPeriod = "Not Set";
                            AnalogTargetFreq = UserInput + Quantifier + Descriptor;
                        }
                        if (control != 1)
                        {
                            SqWaveTargetPeriod = "Not Set";
                            SqWaveTargetFreq = UserInput + Quantifier + Descriptor;
                            SendControl = "b  "; // switch Control to Sq.Wave
                        }
                        if (control == 2) SendControl = "b"; // switch Control to both waves
                        if (Mode == 1) ReadActualSettings();
                        fill(0);
                    }
                    else
                    {
                        if ((Mode == 0 && SweepStep == 0) || SettingTouched[div] != 1) // if single wave not already selected OR in mode 0
                        {
                            SettingTouched[div] = 1;
                            if (control == 2) SettingTouched[div1] = 1;
                            if (Mode == 0 && SweepStep == 0)
                            {
                                Symbol = 1;
                                Quantifier = Quantifier1.get(InputMultiplier + (2 * Symbol)); // retrieve which Quantifier button was last pressed
                                Descriptor = "Hz";
                            }
                            else ReadActualSettings();
                        }
                        else if (SettingTouched[control] > 0) SettingTouched[div] = 0; // (if mode 1 or sweep on), switch off control of this wave only if control of other wave is on
                    }
                }
                else if (MouseJustPressed && SweepStep == 0 && mouseX > (Magnify * 300) + 53 && mouseX < (Magnify * 300) + 373 - (170 * TimerMode) && mouseY > 160 && mouseY < 210) // if we clicked Period
                {
                    MouseJustPressed = false; // debounce
                    int div =  (mouseX - (Magnify * 300)) / 212; // == 0 or 1
                    int control = 1 - div; // opposite to div (to match Arduino's Control)
                    int div1 = div;
                    if (mouseX > (Magnify * 300) + 203 && mouseX < (Magnify * 300) + 222) control = 2; // if area between waves touched
                    if (control == 2) div1 = 1 - div; // if area between waves touched, div1 = opposite wave to div
                    //     println("SettingTouched[" + div + "] = " + SettingTouched[div]);
                    if (SettingTouched[div] == 2 && SettingTouched[div1] == 2 && Descriptor.equals("S") && Mode == 0 && SweepStep == 0)
                    {
                        Delimiter = "m";
                        QuantifyInput((InputMultiplier - 2) * 3);
                        if (control > 0)
                        {
                            if (TimerMode == 0) SendControl = "b "; // switch Control to analog
                            AnalogTargetFreq = "Not Set";
                            AnalogTargetPeriod = UserInput + Quantifier + Descriptor;
                        }
                        if (control != 1)
                        {
                            SendControl = "b  "; // switch Control to Sq.Wave
                            SqWaveTargetFreq = "Not Set";
                            SqWaveTargetPeriod = UserInput + Quantifier + Descriptor;
                        }
                        if (control == 2) SendControl = "b"; // switch Control to both waves
                        if (Mode == 1) ReadActualSettings();
                        fill(0);
                    }
                    else
                    {
                        if (Mode == 0 || SettingTouched[div] != 2)
                        {
                            SettingTouched[div] = 2;
                            if (control == 2) SettingTouched[div1] = 2;
                            if (Mode == 0)
                            {
                                Symbol = 0;
                                Quantifier = Quantifier1.get(InputMultiplier + (2 * Symbol)); // retrieve which Quantifier button was last pressed
                                Descriptor = "S";
                            }
                            else ReadActualSettings();
                        }
                        else if (SettingTouched[control] > 0) SettingTouched[div] = 0; // && if (Mode > 0 || SettingTouched[div] == 1) // if mode 1, switch off control of this wave only if control of other wave is on
                    }
                }
                else if (MouseJustPressed && mouseX > (Magnify * 300) + 53 && mouseX < (Magnify * 300) + 373 - (170 * TimerMode) && mouseY > 225 && mouseY < 274) // if we clicked duty cycle
                {
                    MouseJustPressed = false; // debounce
                    int div =  (mouseX - (Magnify * 300)) / 212; // == 0 or 1
                    if (SweepStep == 0)
                    {
                        int control = 1 - div; // opposite to div (to match Arduino's Control)
                        int div1 = div;
                        if (mouseX > (Magnify * 300) + 203 && mouseX < (Magnify * 300) + 222) control = 2; // if area between waves touched
                        if (control == 2) div1 = 1 - div; // if area between waves touched, div1 = opposite wave to div
                        if (SettingTouched[div] == 3 && SettingTouched[div1] == 3 && Descriptor.equals("%") && Mode == 0)
                        {
                            Delimiter = "d";
                            if (control > 0)
                            {
                                if (TimerMode == 0) SendControl = "b "; // switch Control to analog
                                AnalogTargetPulseW = "Not Set";
                            }
                            if (control != 1)
                            {
                                SendControl = "b  "; // switch Control to Sq.Wave
                                SqWaveTargetPulseW = "Not Set";
                            }
                            if (PApplet.parseFloat(UserInput) > 100)
                            {
                                UserInput = "100.0";
                                if (control > 0) AnalogTargetDuty = "100.0%";
                                if (control != 1) SqWaveTargetDuty = "100.0%";
                            }
                            else if (PApplet.parseFloat(UserInput) > 0)
                            {
                                QuantifyInput(0);
                                if (UserInput.length() > 5) UserInput = UserInput.substring(0, 6);
                                if (control > 0) AnalogTargetDuty = UserInput + "%";
                                if (control != 1) SqWaveTargetDuty = UserInput + "%";
                            }
                            else
                            {
                                String[] m = match(UserInput, "0");
                                if (m != null)
                                {
                                    if (control > 0) AnalogTargetDuty = "0.00%";
                                    if (control != 1) SqWaveTargetDuty = "0.00%";
                                    UserInput = "0.00";
                                    txf1.setText(UserInput);
                                }
                                else
                                {
                                    Info = "             That input is invalid.";
                                    InfoTime = millis() + 300;
                                    SendControl = "";
                                    Delimiter = null;
                                    println("Delimiter = " + Delimiter);
                                }
                            }
                            fill(0);
//            println("UserInput: " + UserInput);
                            QuantifiedInput = UserInput;
                            if (control == 2) SendControl = "b"; // switch Control to both waves
                            if (Mode == 1) ReadActualSettings();
                        }
                        else
                        {
                            if (Mode == 0 || SettingTouched[div] != 3)
                            {
                                SettingTouched[div] = 3;
                                if (control == 2) SettingTouched[div1] = 3;
                                if (Mode == 0)
                                {
                                    Symbol = 2;
                                    Quantifier = "";
                                    Descriptor = "%";
                                }
                                else ReadActualSettings();
                            }
                            else if (SettingTouched[control] > 0) SettingTouched[div] = 0; // && if (Mode > 0 || SettingTouched[div] == 1) // if mode 1, switch off control of this wave only if control of other wave is on
                        }
                    }
                    else //  if (SweepStep != 0)
                    {
                        if (div == 0)
                        {
                            if (SweepStep == 3 && Descriptor.equals("Hz")) // Max freq
                            {
                                QuantifyInput((InputMultiplier - 1) * 3);
                                SweepMaxFreq = UserInput + Quantifier + "Hz";
                                SweepMaxFreqf = PApplet.parseFloat(QuantifiedInput);
                            }
                            else
                            {
                                SweepStep = 3;
                                Symbol = 1;
                                Descriptor = "Hz";
                                Quantifier = Quantifier1.get(InputMultiplier + 2); // retrieve which Quantifier button was last pressed
                            }
                        }
                        else // if (div == 1)
                        {
                            if (SweepStep == 4)
                            {
                                Quantifier = " ";
                                QuantifyInput(0); // Rise time
                                SweepRiseTime = PApplet.parseFloat(UserInput);
                            }
                            else
                            {
                                SweepStep = 4;
                                Symbol = 0;
                                Descriptor = "S";
                                Quantifier = " ";
                            }
                        }
                    }
                }
                else if (MouseJustPressed && mouseX > (Magnify * 300) + 53 && mouseX < (Magnify * 300) + 373 - (170 * TimerMode) && mouseY > 290 && mouseY < 340) // if we clicked Pulse Width
                {
                    MouseJustPressed = false; // debounce
                    int div =  (mouseX - (Magnify * 300)) / 212; // == 0 or 1
                    int control = 1 - div; // opposite to div (to match Arduino's Control)
                    int div1 = div;
                    if (SweepStep == 0)
                    {
                        if (mouseX > (Magnify * 300) + 203 && mouseX < (Magnify * 300) + 222) control = 2; // if area between waves touched
                        if (control == 2) div1 = 1 - div; // if area between waves touched, div1 = opposite wave to div
                        if (SettingTouched[div] == 4 && SettingTouched[div1] == 4 && Descriptor.equals("S") && Mode == 0)
                        {
                            Delimiter = "u";
                            if (PApplet.parseFloat(UserInput) == 0)
                            {
                                QuantifiedInput = "0";
                                InputMultiplier = 0;
                                Quantifier = " n";
                            }
                            else QuantifyInput((InputMultiplier - 1) * 3);
                            if (control > 0)
                            {
                                if (TimerMode == 0) SendControl = "b "; // switch Control to analog
                                AnalogTargetDuty = "Not Set";
                                AnalogTargetPulseW = UserInput + Quantifier + Descriptor;
                            }
                            if (control != 1)
                            {
                                SendControl = "b  "; // switch Control to Sq.Wave
                                SqWaveTargetDuty = "Not Set";
                                SqWaveTargetPulseW = UserInput + Quantifier + Descriptor;
                            }
                            if (control == 2) SendControl = "b"; // switch Control to both waves
                            if (Mode == 1) ReadActualSettings();
                            fill(0);
                        }
                        else
                        {
                            if (Mode == 0 || SettingTouched[div] != 4)
                            {
                                SettingTouched[div] = 4;
                                if (control == 2) SettingTouched[div1] = 4;
                                if (Mode == 0)
                                {
                                    Symbol = 0;
                                    Quantifier = Quantifier1.get(InputMultiplier + (2 * Symbol)); // retrieve which Quantifier button was last pressed
                                    Descriptor = "S";
                                }
                                else ReadActualSettings();
                            }
                            else if (SettingTouched[control] > 0) SettingTouched[div] = 0; // && if (Mode > 0 || SettingTouched[div] == 1) // if mode 1, switch off control of this wave only if control of other wave is on
                        }
                    }
                    else // if (SweepStep != 0)
                    {
                        if (div == 0)
                        {
                            if (SweepStep == 2 && Descriptor.equals("Hz"))
                            {
                                QuantifyInput((InputMultiplier - 1) * 3);
                                SweepMinFreq = UserInput + Quantifier + "Hz"; // Min freq
                                SweepMinFreqf = PApplet.parseFloat(QuantifiedInput);
                            }
                            else
                            {
                                SweepStep = 2;
                                Symbol = 1;
                                Descriptor = "Hz";
                                Quantifier = Quantifier1.get(InputMultiplier + 2); // retrieve which Quantifier button was last pressed
                            }
                        }
                        else // if (div == 1)
                        {
                            if (SweepStep == 5) // Fall time
                            {
                                Quantifier = " ";
                                QuantifyInput(0);
                                SweepFallTime = PApplet.parseFloat(UserInput);
                            }
                            else
                            {
                                SweepStep = 5;
                                Symbol = 0;
                                Descriptor = "S";
                                Quantifier = " ";
                            }
                        }
                    }
                }
                else if (MouseJustPressed && TimerMode > 0 && mouseX > (Magnify * 300) + 221 && mouseX < (Magnify * 300) + 373 && mouseY > 119 && mouseY < 207) // if we clicked TIMER target settings
                {
                    if (mouseY > 119 && mouseY < 135) // if we clicked Days
                    {
                        if (!TimerRun || PApplet.parseInt(UserInput) > 0 || (PeriodH + PeriodM + PeriodS > 0)) PeriodD = PApplet.parseInt(UserInput);
//          println("Days set to: " + PeriodD);
                    }
                    else if (mouseY > 143 && mouseY < 159) // if we clicked Hours
                    {
                        if (!TimerRun || PApplet.parseInt(UserInput) > 0 || (PeriodD + PeriodM + PeriodS > 0)) PeriodH = min(23, PApplet.parseInt(UserInput));
//          println("Hours set to: " + PeriodH);
                    }
                    else if (mouseY > 167 && mouseY < 183) // if we clicked Mins
                    {
                        if (!TimerRun || PApplet.parseInt(UserInput) > 0 || (PeriodD + PeriodH + PeriodS > 0)) PeriodM = min(59, PApplet.parseInt(UserInput));
//          println("Mins set to: " + PeriodM);
                    }
                    else if (mouseY > 191 && mouseY < 207) // if we clicked Secs
                    {
                        if (!TimerRun || PApplet.parseInt(UserInput) > 0 || (PeriodD + PeriodH + PeriodM > 0)) PeriodS = min(59, PApplet.parseInt(UserInput));
//          println("Secs set to:" + PeriodS);
                    }
                    if (TimerRun)
                    {
                        boolean elapsed = false;
                        if (TimerDays != PeriodD)
                        {
                            if (TimerDays > PeriodD) elapsed = true;
                        }
                        else if (TimerHours != PeriodH)
                        {
                            if (TimerHours > PeriodH) elapsed = true;
                        }
                        else if (TimerMins != PeriodM)
                        {
                            if (TimerMins > PeriodM) elapsed = true;
                        }
                        else if (TimerSecs >= PeriodS) elapsed = true;
                        if (!elapsed) // if set time has not elapsed
                        {
                            TimeUp = 0;
                            SendControl = "";
                            Delimiter = null;
                            SerialData.write("rR"); // send reset then TimerRun signal to Arduino.
                            println("SENT: rR");
                        }
                    }
                }
                textSize(15);
                fill(0);
                text("FREQUENCY:", (300 * Magnify) + 58, 111); // shadow
                if (SettingTouched[0] == 1 || (SettingTouched[0] >= 1 && SweepStep > 0)) fill(255, 255, 0);
                else fill(255);
                text("FREQUENCY:", (300 * Magnify) + 57, 110);
                fill(255);
                textSize(18);
                text(AnalogActualFreq, (300 * Magnify) + 57, 130);
                textSize(13);
                if (AnalogTargetFreq.equals("Not Set")) fill(155);
                text("Target  " + AnalogTargetFreq, (300 * Magnify) + 57, 147);
                if (TimerMode == 0)
                {
                    textSize(15);
                    fill(0);
                    text("FREQUENCY:", (300 * Magnify) + 226, 111); // shadow
                    if ((SettingTouched[1] == 1 && SweepStep == 0) || (!SquareWaveSync && SettingTouched[1] >= 1 && SweepStep > 0)) fill(255, 255, 0);
                    else fill(255);
                    text("FREQUENCY:", (300 * Magnify) + 225, 110);
                    if (SquareWaveSync) fill(175);
                    else fill(255);
                    textSize(18);
                    text(SqWaveActualFreq, (300 * Magnify) + 225, 130);
                    textSize(13);
                    if (SqWaveTargetFreq.equals("Not Set")) fill(155);
                    else fill(255);
                    text("Target  " + SqWaveTargetFreq, (300 * Magnify) + 225, 147);
                }
                fill(0);
                if (SweepStep == 0)
                {
                    textSize(15);
                    text("PERIOD:", (300 * Magnify) + 58, 174); // shadow
                    text("DUTY-CYCLE:", (300 * Magnify) + 58, 237); // shadow
                    text("PULSE WIDTH:", (300 * Magnify) + 58, 300); // shadow
                    if (SettingTouched[0] == 2 && SweepStep == 0) fill(255, 255, 0);
                    else fill(255);
                    text("PERIOD:", (300 * Magnify) + 57, 173);
                    fill(255);
                    textSize(18);
                    text(AnalogActualPeriod, (300 * Magnify) + 57, 193);
                    textSize(14);
                    if (AnalogTargetPeriod.equals("Not Set")) fill(155);
                    text("Target " + AnalogTargetPeriod, (300 * Magnify) + 57, 210);
                    textSize(15);
                    fill(0);
                    if (SettingTouched[0] == 3) fill(255, 255, 0);
                    else fill(255);
                    text("DUTY-CYCLE:", (300 * Magnify) + 57, 236);
                    fill(255);
                    textSize(18);
                    text("Actual " + AnalogActualDuty, (300 * Magnify) + 57, 256);
                    textSize(14);
                    if (AnalogTargetDuty.equals("Not Set")) fill(155);
                    text("Target " + AnalogTargetDuty, (300 * Magnify) + 57, 273);
                    textSize(15);
                    fill(0);
                    if (SettingTouched[0] == 4) fill(255, 255, 0);
                    else fill(255);
                    text("PULSE WIDTH:", (300 * Magnify) + 57, 299);
                    fill(255);
                    textSize(18);
                    text(AnalogActualPulseW, (300 * Magnify) + 57, 319);
                    textSize(14);
                    if (AnalogTargetPulseW.equals("Not Set")) fill(155);
                    text("Target " + AnalogTargetPulseW, (300 * Magnify) + 57, 336);
                    if (TimerMode == 0) // && SweepStep == 0)
                    {
                        fill(0);
                        textSize(15);
                        text("PERIOD:", (300 * Magnify) + 226, 174); // shadow
                        text("DUTY-CYCLE:", (300 * Magnify) + 226, 237); // shadow
                        text("PULSE WIDTH:", (300 * Magnify) + 226, 300); // shadow
                        if (SettingTouched[1] == 2 && SweepStep == 0) fill(255, 255, 0);
                        else fill(255);
                        text("PERIOD:", (300 * Magnify) + 225, 173);
                        if (SquareWaveSync) fill(175);
                        else fill(255);
                        textSize(18);
                        text(SqWaveActualPeriod, (300 * Magnify) + 225, 193);
                        textSize(14);
                        if (SqWaveTargetPeriod.equals("Not Set")) fill(155);
                        else fill(255);
                        text("Target " + SqWaveTargetPeriod, (300 * Magnify) + 225, 210);
                        textSize(15);
                        fill(0);
                        if (SettingTouched[1] == 3) fill(255, 255, 0);
                        else fill(255);
                        text("DUTY-CYCLE:", (300 * Magnify) + 225, 236);
                        if (SquareWaveSync) fill(175);
                        else fill(255);
                        textSize(18);
                        text("Actual " + SqWaveActualDuty, (300 * Magnify) + 225, 256);
                        textSize(14);
                        if (SqWaveTargetDuty.equals("Not Set")) fill(155);
                        else fill(255);
                        text("Target " + SqWaveTargetDuty, (300 * Magnify) + 225, 273);
                        textSize(15);
                        fill(0);
                        if (SettingTouched[1] == 4) fill(255, 255, 0);
                        else fill(255);
                        text("PULSE WIDTH:", (300 * Magnify) + 225, 299);
                        if (SquareWaveSync) fill(175);
                        else fill(255);
                        textSize(18);
                        text(SqWaveActualPulseW, (300 * Magnify) + 225, 319);
                        textSize(14);
                        if (SqWaveTargetPulseW.equals("Not Set")) fill(155);
                        else fill(255);
                        text("Target " + SqWaveTargetPulseW, (300 * Magnify) + 225, 336);
                    }
                }
                else if (SweepStep > 0) // && TimerMode == 0) // if Freq Sweep
                {
                    textSize(19);
                    text("FREQ  SWEEP", (300 * Magnify) + 58, 188); // shadow
                    text("RANGE:", (300 * Magnify) + 58, 215);
                    text("FREQ  SWEEP", (300 * Magnify) + 226, 188); // shadow
                    text("TIMING:", (300 * Magnify) + 226, 215);
                    fill(175, 255, 175);
                    text("FREQ  SWEEP", (300 * Magnify) + 57, 187);
                    text("RANGE:", (300 * Magnify) + 57, 214);
                    fill(150, 200, 255);
                    text("FREQ  SWEEP", (300 * Magnify) + 225, 187);
                    text("TIMING:", (300 * Magnify) + 225, 214);
                    textSize(15);
                    fill(0);
                    text("MAX FREQ:", (300 * Magnify) + 58, 247); // shadow
                    text("RISE TIME:", (300 * Magnify) + 226, 247);
                    if (SweepStep == 3) fill(255, 255, 0);
                    else fill(255);
                    text("MAX FREQ:", (300 * Magnify) + 57, 246);
                    if (SweepStep == 4) fill(255, 255, 0);
                    else fill(255);
                    text("RISE TIME:", (300 * Magnify) + 225, 246);
                    fill(255);
                    textSize(18);
                    text(SweepMaxFreq, (300 * Magnify) + 57, 269);
                    text(SweepRiseTime + " Secs", (300 * Magnify) + 225, 269);
                    textSize(15);
                    fill(0);
                    text("MIN FREQ:", (300 * Magnify) + 58, 306); // shadow
                    text("FALL TIME:", (300 * Magnify) + 226, 306);
                    if (SweepStep == 2) fill(255, 255, 0);
                    else fill(255);
                    text("MIN FREQ:", (300 * Magnify) + 57, 305);
                    if (SweepStep == 5) fill(255, 255, 0);
                    else fill(255);
                    text("FALL TIME:", (300 * Magnify) + 225, 305);
                    fill(255);
                    textSize(18);
                    text(SweepMinFreq, (300 * Magnify) + 57, 329);
                    text(SweepFallTime + " Secs", (300 * Magnify) + 225, 329);
                    textSize(14);
                }
                if (TimerMode > 0) // && SweepStep == 0) // if Timer Mode
                {
                    textSize(15);
                    fill(0);
                    text("TARGET TIME:", (300 * Magnify) + 226, 111); // shadow
                    if (TimeUp == 0) text("ELAPSED TIME:", (300 * Magnify) + 226, 237); // shadow
                    else            text("*** TIME IS UP! ***", (300 * Magnify) + 226, 237); // shadow
                    fill(255, 255, 0);
                    text("TARGET TIME:", (300 * Magnify) + 225, 110);
                    fill(255);
                    text(PeriodD + " Days", (300 * Magnify) + 225, 134);
                    text(nf(PeriodH, 2, 0) + " Hours", (300 * Magnify) + 225, 158); // nf(value, num of whole digits, num of decimal places)
                    text(nf(PeriodM, 2, 0) + " Minutes", (300 * Magnify) + 225, 182);
                    fill(255);
                    text(nf(PeriodS, 2, 0) + " Seconds", (300 * Magnify) + 225, 206);
                    if (TimeUp > 0)
                    {
                        fill(255, 255, 0);
                        text("*** TIME IS UP! ***", (300 * Magnify) + 225, 236);
                    }
                    else
                    {
                        if (TimerRun) fill(100, 255, 100);
                        else fill(255);
                        text("ELAPSED TIME:", (300 * Magnify) + 225, 236);
                    }
                    fill(255);
                    text(TimerDays + " Days", (300 * Magnify) + 225, 260);
                    text(nf(TimerHours, 2, 0) + " Hours", (300 * Magnify) + 225, 284);
                    text(nf(TimerMins, 2, 0) + " Minutes", (300 * Magnify) + 225, 308);
                    fill(255);
                    text(nf(TimerSecs, 2, 0) + " Seconds", (300 * Magnify) + 225, 332);
                }
                fill(255);
                textSize(13);
                text("Clear Back", (300 * Magnify) + 48, max(388, (175 * KeypadPos) + 38));
                textSize(18);
                DrawButtons(264, 20, 25, 20, 4);
                if (MouseJustPressed && mouseX > ((Magnify * 300) + 48) && mouseX < ((Magnify * 300) + 120) && mouseY > max(370, (175 * KeypadPos) + 20) && mouseY < max(390, (175 * KeypadPos) + 40)) // if Clear or Backspace:
                {
                    MouseJustPressed = false;
                    if (mouseX < (Magnify * 300) + 83) // Clear
                    {
                        UserInput = "";
                        txf1.setText(UserInput);
                    }
                    else if (mouseX > (Magnify * 300) + 87) // Backspace
                    {
                        UserInput  = UserInput.substring(0, max(0,UserInput.length() - 1));
                        txf1.setText(UserInput);
                    }
                }
                else if (MouseJustPressed && mouseX > ((Magnify * 300) + 216) && mouseX < ((Magnify * 300) + 258) && mouseY > max(370, (175 * KeypadPos) + 20) && mouseY < max(390, (175 * KeypadPos) + 40)) // if symbol (quantifier + descriptor):
                {
                    if (Symbol < 2) Symbol++;
                    else Symbol = 0;
                    Descriptor = Descriptor1.get(Symbol);
                    if (Symbol < 2) Quantifier = Quantifier1.get(InputMultiplier + (2 * Symbol)); // retrieve which Quantifier button was last pressed
                    else Quantifier = "";
                }
                else if (mousePressed && mouseX > ((Magnify * 300) + 264) && mouseX < ((Magnify * 300) + 383) && mouseY > max(370, (175 * KeypadPos) + 20) && mouseY < max(393, (175 * KeypadPos) + 43)) // if quantifier buttons:
                {
                    InputMultiplier = (mouseX - ((Magnify * 300) + 264)) / 30; // remember which Quantifier button was last pressed
                    if (Symbol < 2) Quantifier = Quantifier1.get(min(5, (mouseX - ((Magnify * 300) + 264)) / 30) + (2 * Symbol)); // calculate which button was pressed
                    else Quantifier = "";
                    ButtonPressed(264, 20, 25, 20, 4);
                }
                fill(255);
                text(Quantifier, (300 * Magnify) + 216, max(389, (175 * KeypadPos) + 39));
                text(Descriptor, (300 * Magnify) + 234, max(389, (175 * KeypadPos) + 39));
                textSize(15);
                textSize(18);
                if (Mode == 0) DrawButtons(43, 49, 26, 26, 0);
                if (mousePressed && mouseX > ((Magnify * 300) + 44) && mouseX < ((Magnify * 300) + 383) && mouseY > max(400, (175 * KeypadPos) + 49) && mouseY < max(427, (175 * KeypadPos) + 77)) // if KEYPAD  or  <<<<<<<< >>>>>>> Button:
                {
                    if (Mode == 0) ButtonPressed(43, 49, 26, 26, 0); // if keypad
                    else // if <<<<<<<< >>>>>>> Button:
                    {
                        if (Connected && (SettingTouched[0] > 0 || (SettingTouched[1] > 0 && TimerMode == 0))) // if at least one wave setting is selected (b_ already sent if in Timer Mode)
                        {
                            DrawDownUpButton(1); // also switches to Mode 2
                            if (SettingTouched[0] == 0)
                            {
                                SerialData.write("b  "); // send SqWave Control signal to Arduino
                                //          println("DATA SENT: b__");
                            }
                            else if (SettingTouched[1] == 0)
                            {
                                SerialData.write("b "); // send analog Control signal to Arduino
                                //         println("DATA SENT: b_");
                            }
                            //        if (SettingTouched[0] == SettingTouched[1]) SerialData.write("b"); // send both Control signal to Arduino // comment out for proportional operation
                            SendControl = ""; // ensure conflicting Control signal isn't sent later
                        }
                        else
                        {
                            if (!Connected)
                            {
                                DrawDownUpButton(0);
                                Info = "                   No connection to Arduino!";
                            }
                            else
                            {
                                textSize(15);
                                if (TimerMode > 0 && SettingTouched[0] == 0) text("   Select one of the settings above left first!", (Magnify * 300) + 44, max(418, (175 * KeypadPos) + 68));
                                else                                         text("  Select one or two of the above settings first!", (Magnify * 300) + 44, max(418, (175 * KeypadPos) + 68));
                            }
                        }
                    }
                }
                else if (Mode > 0) DrawDownUpButton(0);
                textSize(15);
                if (MouseJustPressed && mouseY > 7 && mouseY < 23) // if TOP ROW:
                {
                    if (mouseX > 16 && mouseX < 57) // if we clicked OPEN
                    {
                        MouseJustPressed = false; // debounce
                        selectInput("Select a file to open:", "fileSelected");
                    }
                    else if (mouseX > (Magnify * 97) - 2 && mouseX < (Magnify * 97) + 54) // if DELETE
                    {
                        MouseJustPressed = false; // debounce
                        Saving = 2;
                        selectInput("Select a file to DELETE, then press Open.", "fileSelected");
                    }
                    else if (mouseX > (Magnify * 197) - 9 && mouseX < (Magnify * 197) + 31) // if SAVE
                    {
                        MouseJustPressed = false; // debounce
                        Saving = 1; // true;
                        selectOutput("Select or create a file to save to:", "fileSelected");
                    }
                    else if (mouseX > (Magnify * 300) - 37 && mouseX < (Magnify * 300) + 24) // if UPLOAD
                    {
                        if (Connected && DrawWave == 2 && ArbitraryPointNumber <= 4096)
                        {
                            MouseJustPressed = false; // debounce
                            Delimiter = "a";
                            //          println("sent a");
                            noStroke();
                            fill(40);
                            rect((Magnify * 300) - 37, 7, 65, 20);
                            fill(255);
                            textSize(14);
                            fill(0, 255, 0);
                            text("Uploading...", (Magnify * 300) - 40, 22);
                        }
                        else
                        {
                            if         (!Connected) Info = "                   No connection to Arduino!";
                            else if (DrawWave != 2) Info = "               Please open or draw a wave first!";
                            else                    Info = " The wave has " + ArbitraryPointNumber + " waypoints. Maximum is 4096.";
                            InfoTime = millis();
                        }
                    }
                    else if (mouseX > (Magnify * 300) + 60 && mouseX < (Magnify * 300) + 123) // if OPTIONS
                    {
                        MouseJustPressed = false; // debounce
                        createOptionsWindow();
                    }
                    else if (mouseX > (Magnify * 300) + 160 && mouseX < (Magnify * 300) + 196) // if HELP
                    {
                        MouseJustPressed = false; // debounce
                        mousePressed = false;
                        if (SelectedSubject < 0) createHelpWindow(); // so 2nd help window can't be opened & selected subject remembered
                    }
                }
                else if ((Scrolling || mouseX <= (Magnify * 300) + 42) && mouseY > 35 && mouseY < (175 * Magnify) + 79) // if DRAWING AREA: =================================================================================
                {
                    if (mousePressed)
                    {
                        int x = mouseX;
                        int y = mouseY;
                        if (abs(x - Oldx) > abs(y - Oldy) && millis() > ScrollTime + 250) Scrolling = false;
                        else if (abs(x - Oldx) < abs(y - Oldy)) ScrollTime = millis();
                        if (ProgDraw || x != Oldx) Oldx = x;
                        if (y != Oldy) Oldy = y;
                        if (!Scrolling && x >= 20 && x <= (Magnify * 300) + 20 && ArbitraryPointNumber == 300 * Magnify) x = max(x, MinX); // prevents drawing points in right scroll area in progressive mode!
                        if (!Scrolling && x >= 20 && x <= (Magnify * 300) + 20 && ArbitraryPointNumber == 300 * Magnify)
                        {
                            //           x = constrain(x, MinX, (Magnify * 300) + 20);   // points on PC: 20 - 320 (0 - 300)
                            y = constrain(y, 57, (Magnify * 175) + 57);  // 30 - 205 (0 - 175)
                            if (Autofill == 2) // if erasing: =================================================================================
                            {
                                stroke(40);
                                if (ArbitraryWave[max(mouseX - 20, 0)] >= 0 && abs((175 * Magnify) + 57 - (ArbitraryWave[max(mouseX - 20, 0)] / (23.4f / Magnify)) - y) <= EditSize) // if point is within erasing area: (comments)
                                {
                                    if (ArbitraryWaveStep[max(mouseX - 20, 0)] == -1) // if unstepped point
                                    {
                                        if (mouseX - 20 > 0 && mouseX - 20 < ArbitraryPointNumber) ArbitraryWave[max(mouseX - 20, 1)] = -1;
                                        else if (mouseX - 20 == ArbitraryPointNumber) ArbitraryWaveStep[0] = ArbitraryWaveStep[ArbitraryPointNumber] = -1;
                                        line(mouseX, max(y - EditSize, 57), mouseX, min(y + EditSize, (Magnify * 175) + 57));
                                    }
                                    else // if stepped point (comments)
                                    {
                                        if (mouseX - 20 > 0 && mouseX - 20 < ArbitraryPointNumber)
                                        {
                                            ArbitraryWave[max(mouseX - 20, 1)] = ArbitraryWaveStep[max(mouseX - 20, 1)];
                                            ArbitraryWaveStep[max(mouseX - 20, 1)] = -1;
                                        }
                                        else if (mouseX - 20 == ArbitraryPointNumber) ArbitraryWaveStep[0] = ArbitraryWaveStep[ArbitraryPointNumber] = -1;
                                        line(mouseX, 57, mouseX, (Magnify * 175) + 57);
                                    }
                                }
                                if (ArbitraryWaveStep[max(mouseX - 20, 1)] >= 0 && abs((175 * Magnify) + 57 - y - (ArbitraryWaveStep[max(mouseX - 20, 1)] / (23.4f / Magnify))) <= EditSize) // if stepped part of point is within erasing area:
                                {
                                    if (mouseX - 20 < ArbitraryPointNumber) ArbitraryWaveStep[max(mouseX - 20, 1)] = -1;
                                    else ArbitraryWaveStep[0] = ArbitraryWaveStep[ArbitraryPointNumber] = -1;
                                    line(mouseX, 57, mouseX, (Magnify * 175) + 57);
                                }
                                if (mouseX - 20 == 0) Wave0Filled = false;
                                if (mouseX - 20 == ArbitraryPointNumber) WaveStep0Filled = false;
                            }
                            else // if drawing: =================================================================================
                            {
                                stroke(0, 180, 255); // light blue
                                if (ProgDraw && x > 19 && x <= MinX) TempX = MinX; // if "PROGRESSIVE DRAW" selected
                                else TempX = x;
                                int yCalc = round(((Magnify * 175) - y + 57) * (23.4f / Magnify));
                                if (ArbitraryWaveStep[TempX - 20] == -1 && ArbitraryWave[TempX - 20] > -1 && ArbitraryWave[TempX - 20] != yCalc)
                                {
                                    ArbitraryWaveStep[TempX - 20] = ArbitraryWave[TempX - 20]; // if stepWave not changed & Wave has been changed (if 1st time 2 values assigned to this point, so only update step variable once)
                                }
                                if (TempX - 20 < ArbitraryPointNumber) ArbitraryWave[TempX - 20] = yCalc;
                                else ArbitraryWaveStep[0] = ArbitraryWaveStep[ArbitraryPointNumber] = yCalc;
                                //         println("x = " + x + "  y = " + y + "  ArbitraryWaveStep[" + (TempX - 20) + "]" + ArbitraryWaveStep[TempX - 20] + "  ArbitraryWave[" + (x - 20) + "]" + ArbitraryWave[TempX - 20]);
                                if (ProgDraw) MinX = x;
                                point(TempX, y);
                                if (TempX - 20 == 0)
                                {
                                    Wave0Filled = true;
                                    DrawnArbitrarySetup = 0;
                                }
                                if (TempX - 20 == ArbitraryPointNumber)
                                {
                                    WaveStep0Filled = true;
                                    DrawnArbitrarySetup = 0;
                                }
                                Drawing = true;
                            }
                        }
                        else if (!Scrolling && x >= 20 && x <= (Magnify * 300) + 20) // if (mousePressed) // && ArbitraryPointNumber != (300 * Magnify)
                        {
                            mousePressed = false; // debounce
                            if (ArbitraryPointNumber <= 1500 && ArbitraryPointNumber % 300 == 0) JOptionPane.showMessageDialog(frame, "This wave has " + ArbitraryPointNumber + " waypoints, but the window has " + (300 * Magnify) + ".\nTo edit this wave you need to change the program options to " + ArbitraryPointNumber + " waypoints.");
                            else  JOptionPane.showMessageDialog(frame, "This wave has " + ArbitraryPointNumber + " waypoints, but the window has " + (300 * Magnify) + ".\nThe number of waypoints must be a multiple of 300, and no more than 1500.\nTherefore, this program is unable to edit the wave. But you can still upload it.");
                        }
                        if (Scrolling || x <= 20 || (x >= (Magnify * 300) + 20  && x <= (Magnify * 300) + 42)) // if (x >= 0 && MinX == 20) // move start / end markers:
                        {
                            y = constrain(y, 57, (175 * Magnify) + 57);
                            if (!Wave0Filled && ((Scrolling && x < 150) || x < 20))
                            {
                                stroke(5);
                                line(17, 55, 17, (175 * Magnify) + 59); // erase outside left marker
                                stroke(40);
                                line(18, 55, 18, (175 * Magnify) + 59); // erase middle left marker
                                point(19, (175 * Magnify) + 58); // bottom left corner
                                stroke(70);
                                line(19, 56, 19, (175 * Magnify) + 57); // erase inside left marker
                                fill(255, 255, 0); // yellow
                                stroke(255, 255, 0); // yellow
                                rect(17, y - 1, 2, 2); // draw start marker
                                ArbitraryWave[0] = round(((Magnify * 175) - (y - 57)) * (23.4f / Magnify)); // start of wave new position
                                if (!WaveStep0Filled) ArbitraryWaveStep[0] = -1;
                            }
                            if (!WaveStep0Filled)
                            {
                                stroke(5);
                                line((300 * Magnify) + 21, 57, (300 * Magnify) + 21, (175 * Magnify) + 58); // erase inside right marker
                                stroke(40);
                                point((300 * Magnify) + 21, 56); // top right corner
                                line((300 * Magnify) + 22, 56, (300 * Magnify) + 22, (175 * Magnify) + 58); // erase middle of right marker
                                stroke(70);
                                line((300 * Magnify) + 23, 55, (300 * Magnify) + 23, (175 * Magnify) + 59); // erase outside right marker
                                fill(255, 255, 0); // yellow
                                stroke(255, 255, 0); // yellow
                                ArbitraryWaveStep[0] = round(((Magnify * 175) - (y - 57)) * (23.4f / Magnify)); // make point 0 a step point, end of wave new position
                                rect((300 * Magnify) + 21, y - 1, 2, 2); // draw end marker
                            }
                            Scrolling = true;
                        }
                        delay(15); // determines rate of drawing dots
                    }
                }
                else if (mousePressed && mouseY > (Magnify * 175) + 89 && mouseY < (Magnify * 175) + 105) // if ROW UNDER DRAWING AREA: =====================================================================
                {
                    if ((MouseJustPressed || millis() < TouchTime + 400) && mouseX > 16 && mouseX < 66) // if CLEAR
                    {
                        mousePressed = false; // debounce
                        ArbitraryPointNumber = 300 * Magnify;
                        Drawing = false; // finished drawing
                        MinX = 20; // reset minimum value for x
                        Oldx = 0;
                        OpenedWave = null;
                        Wave0Filled = false;
                        WaveStep0Filled = false;
                        fill(40);
                        noStroke();
                        rect(18, 38, (300 * Magnify), 16); // erase file name
                        for(int i = 1; i <= (300 * Magnify) - 1; i++) // don't reset points 0 & 300 unless clear touched a 2nd time (below)
                        {
                            ArbitraryWave[i] = -1; // make points = -1 to indicate they're not set
                            ArbitraryWaveStep[i] = -1;
                        }
                        if (millis() < TouchTime + 400) // DrawnArbitrarySetup = 1; // if clear touched 1st time clear drawing area
                        {
                            ArbitraryWave[0] = 2047; // start of wave at centre value
                            ArbitraryWave[ArbitraryPointNumber] = 2047; // just past end of wave at centre value
                            ArbitraryWaveStep[0] = -1; // start of wave - no step
                            ArbitraryWaveStep[ArbitraryPointNumber] = -1; // just past end of wave - no step
                        }
                        DrawnArbitrarySetup = 0; // redraw markers & clear drawing area
                        DrawWave = 0;
                        TouchTime = millis();
                    }
                    else if (MouseJustPressed && mouseX > (Magnify * 97) - 10 && mouseX < (Magnify * 97) + 85) // if FREE-HAND / PROGRESSIVE DRAW
                    {
                        fill(40);
                        noStroke();
                        rect((Magnify * 97) - 12, (Magnify * 175) + 90, 110, 16); // cover old text, as text background is transparent!
                        fill(255);
                        MouseJustPressed = false; // debounce
                        if (ProgDraw)
                        {
                            text("  FREE-HAND ", (Magnify * 97) - 10, (Magnify * 175) + 104);
                            ProgDraw = false;
                            MinX = 20; // reset minimum value for x
                        }
                        else
                        {
                            text("PROGRESSIVE", (Magnify * 97) - 10, (Magnify * 175) + 104);
                            ProgDraw = true;
                        }
                    }
                    else if (MouseJustPressed && mouseX > (Magnify * 197) + 7 && mouseX < (Magnify * 197) + 77) // if AUTOFILL / MANUAL FILL / ERASE
                    {
                        fill(40);
                        noStroke();
                        rect((Magnify * 197) + 3, (Magnify * 175) + 90, 80, 16); // cover old text, as text background is transparent!
                        fill(255);
                        MouseJustPressed = false; // debounce
                        if (Autofill > 0) Autofill--;
                        else Autofill = 2;
                        if      (Autofill == 1) text("AUTOFILL", (Magnify * 197) + 7, (Magnify * 175) + 104);
                        else if (Autofill == 0) text("MAN FILL", (Magnify * 197) + 10, (Magnify * 175) + 104);
                        else                    text("  ERASE ", (Magnify * 197) + 9, (Magnify * 175) + 104);
                        TouchTime = millis();
                    }
                    else if (MouseJustPressed && mouseX > (Magnify * 300) - 9 && mouseX < (Magnify * 300) + 27) // if FILL
                    {
                        MouseJustPressed = false; // debounce
                        DrawWave = 1;
                    }
                }
                if (MouseJustPressed && mouseY > max(440, (175 * KeypadPos) + 90) && mouseY < max(455, (175 * KeypadPos) + 105)) // if ROW UNDER CONTROL AREA: =====================================================================
                {
                    textSize(15);
                    if (mouseX > (Magnify * 300)  + 60 && mouseX < (Magnify * 300) + 102) // if MODE: (or FREQ SWEEP GO BUTTON)
                    {
                        MouseJustPressed = false; // debounce
                        noStroke();
                        fill(40);
                        rect((Magnify * 300) + 58, max(440, (175 * KeypadPos) + 90), 47, 18);
                        fill(255);
                        if (SweepStep == 0) // if not in freq sweep operation, toggle Mode
                        {
                            text("MODE", (Magnify * 300) + 60, max(454, (175 * KeypadPos) + 104));
                            textSize(18);
                            if (Mode > 0)
                            {
                                Mode = 0;
                                Info = "";
                                DrawButtons(43, 49, 26, 26, 0); // draw keypad
                                InputMultiplier = RememberInputMultiplier; // retrieve which Quantifier button was last pressed (in Mode 0)
                                if (Symbol < 2) Quantifier = Quantifier1.get(InputMultiplier + (2 * Symbol)); // redraw Quantifier to match which Quantifier button was last pressed (in Mode 0)
                                else Quantifier = "";
                            }
                            else
                            {
                                Mode = 1;
                                RememberInputMultiplier = InputMultiplier; // remember which Quantifier button was last pressed (in Mode 0)
                                ReadActualSettings();
                                DrawDownUpButton(0); // 0 = unpressed
                                if (SettingTouched[0] == 0 && SettingTouched[1] == 0) Info = "Please select the wave settings you want to control.";
                                if (SettingTouched[0] == 0 && TimerMode > 0)          Info = "Please select the wave setting you want to control.";
                            }
//            println("Mode = " + Mode);
                        }
                        else // if (SweepStep > 0)
                        {
                            if (SweepStep < 6) // if freq sweep not running
                            {
                                if (Connected && SweepMinFreqf > 0 && SweepMaxFreqf > SweepMinFreqf && SweepRiseTime + SweepFallTime > 0 && ((SquareWaveSync && SettingTouched[0] > 0) || (!SquareWaveSync && (SettingTouched[0] > 0 || SettingTouched[1] > 0)))) // if connected & user settings are valid & wave selection is correct
                                {
                                    if (SquareWaveSync || SettingTouched[1] == 0) SerialData.write("b "); // send analog Control signal to Arduino
                                    else if (!SquareWaveSync && SettingTouched[0] == 0) SerialData.write("b  "); // send SqWave Control signal to Arduino
                                    else if (!SquareWaveSync && SettingTouched[0] > 0 && SettingTouched[1] > 0) SerialData.write("b"); // send both Control signal to Arduino
                                    SendControl = ""; // ensure conflicting Control signal isn't sent later
                                    delay(100);
                                    SweepStep = SweepStep + 5; // above 5 = sweep running
                                    fill(255, 0, 0);
                                    text("STOP", (Magnify * 300) + 62, max(454, (175 * KeypadPos) + 104));
                                    SerialData.write("s" + SweepMinFreqf + "s" + SweepMaxFreqf + "s" + SweepRiseTime + "s" + SweepFallTime + "s"); // send signal to Arduino
                                    println("SENT: s" + SweepMinFreqf + "s" + SweepMaxFreqf + "s" + SweepRiseTime + "s" + SweepFallTime + "s"); // send signal to Arduino
                                }
                                else // if not connected or user settings are invalid or no wave settings are selected
                                {
                                    if (!Connected)                              Info = " Can't sweep frequency - not connected to Arduino.";
                                    else if (SweepMinFreqf == 0)                 Info = "                Min Freq can't be zero.";
                                    else if (SweepMaxFreqf == 0)                 Info = "                Max Freq can't be zero.";
                                    else if (SweepMaxFreqf <= SweepMinFreqf)     Info = "        Max Freq must be higher than Min Freq.";
                                    else if (SweepRiseTime + SweepFallTime == 0) Info = "        Both Rise & Fall Times can't be zero.";
                                    else if (Float.isNaN(SweepRiseTime))         Info = "               Rise Time is not a number.";
                                    else if (Float.isNaN(SweepFallTime))         Info = "               Fall Time is not a number.";
                                    else if (SettingTouched[0] == 0 && SettingTouched[1] == 0) Info = "Please select at least one of the frequency settings.";
                                    else if (SquareWaveSync)                     Info = "    Analogue wave must be selected if Sync is on.";
                                    InfoTime = millis() + 300;
                                    fill(0, 255, 0);
                                    text("GO", (Magnify * 300) + 70, max(454, (175 * KeypadPos) + 104));
                                }
                            }
                        }
                    }
                    if (TimerMode == 0 && mouseX > (Magnify * 300) + 146 && mouseX < (Magnify * 300) + 284) // if FREQUENCY SWEEP BUTTON:
                    {
                        MouseJustPressed = false; // debounce
                        noStroke();
                        fill(40);
                        rect((Magnify * 300) + 58, max(440, (175 * KeypadPos) + 90), 235, 18);
                        fill(255);
                        if (SweepStep == 0) // if freq sweep not running
                        {
                            if (Mode > 0)
                            {
                                Mode = 0;
                                Info = "";
                                DrawButtons(43, 49, 26, 26, 0); // draw keypad
                                textSize(15);
                            }
                            SweepStep = 1;
                            text(" FREQ SWEEP EXIT", (Magnify * 300) + 148, max(454, (175 * KeypadPos) + 104));
                            fill(0, 255, 0);
                            text("GO", (Magnify * 300) + 70, max(454, (175 * KeypadPos) + 104));
                        }
                        else
                        {
                            SweepStep = 0;
                            text("MODE", (Magnify * 300) + 60, max(454, (175 * KeypadPos) + 104));
                            text("FREQUENCY SWEEP", (Magnify * 300) + 144, max(454, (175 * KeypadPos) + 104));
                        }
                    }
                    if (TimerMode > 0 && mouseX > (Magnify * 300) + 195 && mouseX < (Magnify * 300) + 233) // if TIMER GO / STOP BUTTON:
                    {
                        if (PeriodD + PeriodH + PeriodM + PeriodS > 0) // if not set to zero
                        {
                            noStroke();
                            fill(40);
                            rect((Magnify * 300) + 194, max(440, (175 * KeypadPos) + 90), 40, 18);
                            fill(255);
                            TimerRun = !TimerRun;
                            TimerSecs = 0; // reset timer
                            TimerMins = 0;
                            TimerHours = 0;
                            TimerDays = 0;
                            TimeUp = 0;
                            fill(80, 50, 50); // dim red
                            rect((300 * Magnify) + 224, 248, 148, 16); // max(330, (175 * KeypadPos) - 20)); // Right settings area
                            rect((300 * Magnify) + 224, 272, 20, 61); // max(330, (175 * KeypadPos) - 20)); // Right settings area
                            fill(255);
                            text(TimerDays + " Days", (300 * Magnify) + 225, 260);
                            text(nf(TimerHours, 2, 0), (300 * Magnify) + 225, 284);
                            text(nf(TimerMins, 2, 0), (300 * Magnify) + 225, 308);
                            fill(255);
                            text(nf(TimerSecs, 2, 0), (300 * Magnify) + 225, 332);
                            if (Connected)
                            {
                                if (TimerRun) Delimiter = "R";
                                else          Delimiter = "r";
                                SerialData.write(Delimiter); // send TimerRun signal to Arduino.
                                println("SENT: " + Delimiter);
                                TouchTime = millis(); // wait for response
                                ReadSerialData(true); // recheck if serial data confirmed or cancelled
                            }
                            if (TimerRun)
                            {
                                StartTime = millis() / 1000;
                                fill(255, 0, 0);
                                text("STOP", (Magnify * 300) + 194, max(454, (175 * KeypadPos) + 104));
                                fill(100, 255, 100);
                                text("ELAPSED TIME:", (300 * Magnify) + 225, 236);
                            }
                            else // if (!TimerRun)
                            {
                                TimeUp = 0;
                                fill(0, 255, 0);
                                text("GO", (Magnify * 300) + 202, max(454, (175 * KeypadPos) + 104));
                            }
                        }
                        else
                        {
                            Info = "              Target time can't be set to zero.";
                            InfoTime = millis() + 300;
                        }
                    }
                    if (!TimerRun && SweepStep == 0 && mouseX > (Magnify * 300) + 312 && mouseX < (Magnify * 300) + 385) // ========= if TIMER BUTTON:
                    {
                        MouseJustPressed = false; // debounce
                        noStroke();
                        fill(40);
                        rect((Magnify * 300) + 145, max(440, (175 * KeypadPos) + 90), 240, 18);
                        fill(255);
                        QuantifiedInput = "";
                        if (TimerMode == 0)
                        {
                            TimerMode = 1;
                            if (Connected)
                            {
                                SendControl = "b ";
                                Delimiter = "T";
                            }
                            else
                            {
                                Info = "         Running without connection to Arduino.";
                                InfoTime = millis() + 300;
                            }
                            text("EXIT TIMER", (Magnify * 300) + 298, max(454, (175 * KeypadPos) + 104));
                            fill(0, 255, 0);
                            text("GO", (Magnify * 300) + 202, max(454, (175 * KeypadPos) + 104));
                        }
                        else if (TimerMode > 0)
                        {
                            TimerMode = 0;
                            if (Connected)
                            {
                                SendControl = "";
                                Delimiter = "t";
                            }
                            text("FREQUENCY SWEEP", (Magnify * 300) + 144, max(454, (175 * KeypadPos) + 104));
                            text("TIMER", (Magnify * 300) + 323, max(454, (175 * KeypadPos) + 104));
                        }
                    }
                }
                if (DrawnArbitrarySetup < 2 || DrawWave == 1)
                {
                    fill(40);
                    noStroke();
                    if (!Drawing) rect(20, 57, (300 * Magnify) + 1, (175 * Magnify) + 1); // clear drawing area
                    if (DrawnArbitrarySetup < 1 || DrawWave == 1)
                    {
                        stroke(5);
                        line(17, 55, 17, (175 * Magnify) + 59); // erase outside left marker
                        stroke(40);
                        line(18, 55, 18, (175 * Magnify) + 59); // erase middle left marker
                        point(19, (175 * Magnify) + 58); // bottom left corner
                        point(19, (175 * Magnify) + 58); // bottom left corner - doesn't fully cover the pixel 1st time!!!
                        stroke(70);
                        line(19, 56, 19, (175 * Magnify) + 57); // erase inside left marker
                        stroke(5);
                        line((300 * Magnify) + 21, 57, (300 * Magnify) + 21, (175 * Magnify) + 58); // erase inside right marker
                        stroke(40);
                        point((300 * Magnify) + 21, 56); // top right corner
                        point((300 * Magnify) + 21, 56); // top right corner - doesn't fully cover the pixel 1st time!!!
                        line((300 * Magnify) + 22, 56, (300 * Magnify) + 22, (175 * Magnify) + 58); // erase middle of right marker
                        stroke(70);
                        line((300 * Magnify) + 23, 55, (300 * Magnify) + 23, (175 * Magnify) + 59); // erase outside right marker
                        if (Wave0Filled || DrawWave == 1)
                        {
                            fill(0, 255, 255); // cyan
                            stroke(0, 210, 210); // cyan
                        }
                        else
                        {
                            fill(255, 255, 0); // yellow
                            stroke(255, 255, 0); // yellow
                        }
                        rect(17, round((175 * Magnify) + 56 - (ArbitraryWave[0] / (23.4f / Magnify))), 2, 2); // draw start marker
                        if (WaveStep0Filled || DrawWave == 1)
                        {
                            fill(0, 255, 255); // cyan
                            stroke(0, 210, 210); // cyan
                        }
                        else
                        {
                            fill(255, 255, 0); // yellow
                            stroke(255, 255, 0); // yellow
                        }
                        if (ArbitraryWaveStep[0] == -1) rect((300 * Magnify) + 21, round((175 * Magnify) + 56 - (ArbitraryWave[0] / (23.4f / Magnify))), 2, 2); // draw end marker
                        else rect((300 * Magnify) + 21, round((175 * Magnify) + 56 - (ArbitraryWaveStep[0] / (23.4f / Magnify))), 2, 2); // draw end marker
                        //      println("End marker = " + ((175 * Magnify) + 57 - (ArbitraryWave[ArbitraryPointNumber] / (23.4 / Magnify))));
                    }
                    DrawnArbitrarySetup = 2;  // int(((Magnify * 175) - (y - 57)) * (23.4 / Magnify));
                }
                if (DrawWave == 1) // Display Arbitrary Wave on PC:
                {
                    fill(40);
                    noStroke();
                    ArbitraryWave[ArbitraryPointNumber] = ArbitraryWave[0];
                    ArbitraryWaveStep[ArbitraryPointNumber] = ArbitraryWaveStep[0];
                    if (!Drawing || Autofill == 2) rect(20, 57, (300 * Magnify), (175 * Magnify) + 1); // if not drawing or if erasing, clear drawing area
                    stroke(0, 255, 255);
                    float diff = PApplet.parseFloat(300 * Magnify) / ArbitraryPointNumber; // proportional difference between waypoints in wave and points in drawing window, 1 if equal
                    int lastFilledPointValue = ArbitraryWave[0];
                    int unfilledPoints = 0;
                    float stepValue = 0;
                    for(int point = 1; point <= ArbitraryPointNumber; point++) // start from 2nd point, 1 (1st point is pre-filled)
                    {
                        if (ArbitraryWave[point] == -1) unfilledPoints++; // if current point not filled, count unfilled points
                        else                                             // if current point filled, check whether previous point is filled
                        {
                            if (ArbitraryWave[point - 1] == -1)          // if previous point not filled, fill all previous unfilled points
                            {
                                if (ArbitraryWaveStep[point] > -1) stepValue = PApplet.parseFloat(ArbitraryWaveStep[point] - lastFilledPointValue) / (unfilledPoints + 1); // if point is a wave-step, read level from 'step' variable
                                else stepValue = PApplet.parseFloat(ArbitraryWave[point] - lastFilledPointValue) / (unfilledPoints + 1);                                  // otherwise it's a normal point so read level from usual variable
                                float level = lastFilledPointValue;
                                for(int i = point - unfilledPoints; i < point; i++)
                                {
                                    level += stepValue;
                                    ArbitraryWave[i] = round(level);
                                    line((i * diff) + 20 - diff, (175 * Magnify) + 57 - round(ArbitraryWave[i - 1] / (23.4f / Magnify)), (i * diff) + 20, (175 * Magnify) + 57 - round(ArbitraryWave[i] / (23.4f / Magnify))); // join points (on PC) just filled
                                }
                                unfilledPoints = 0;
                            }
                            if (ArbitraryWaveStep[point] > -1) // if current point is a step point
                            {
                                line((point * diff) + 20 - diff, (175 * Magnify) + 57 - round(ArbitraryWave[point - 1] / (23.4f / Magnify)), (point * diff) + 20, (175 * Magnify) + 57 - round(ArbitraryWaveStep[point] / (23.4f / Magnify))); // if stepped point, join point (on PC) to previous point ((point * diff) is previous point on PC)
                                if (point < ArbitraryPointNumber) line((point * diff) + 20, (175 * Magnify) + 57 - round(ArbitraryWaveStep[point] / (23.4f / Magnify)), (point * diff) + 20, (175 * Magnify) + 57 - round(ArbitraryWave[point] / (23.4f / Magnify))); // if stepped point, join points (on PC) in vertical part of step
                            }
                            else line((point * diff) + 20 - diff, (175 * Magnify) + 57 - round(ArbitraryWave[point - 1] / (23.4f / Magnify)), (point * diff) + 20, (175 * Magnify) + 57 - round(ArbitraryWave[point] / (23.4f / Magnify))); // join points
                            lastFilledPointValue = ArbitraryWave[point];
                        }
                    }
                    Wave0Filled = true;
                    WaveStep0Filled = true;
                    Drawing = false;
                    DrawWave = 2; // drawn
                }
            }
        }
        if (OpenedWave != null)
        {
            fill(40);
            noStroke();
            rect(17, 37, (300 * Magnify), 16);
            fill(0, 220, 220);
            textSize(15);
            if (TempX == -1) // if no tab found in file
            {
                fill(255, 0, 0);
                text(OpenedWave + " seems to be corrupted.", 18, 50);
                TempX = 0;
            }
            else text(OpenedWave + " - " + ArbitraryPointNumber + " waypoints", 18, 50);
            OpenedWave = null;
        }
        MouseJustPressed = false;
        if (!mousePressed && millis() > 2500 && !TimerRun && DrawWave != 1)
        {
            Scrolling = false;
            if (mouseX > ((Magnify * 300) + 115) && mouseX < ((Magnify * 300) + 218) && mouseY > max(370, (175 * KeypadPos) + 20) && mouseY < max(390, (175 * KeypadPos) + 40)) // if text field:
            {
                frameRate(5); // keep curser flashing
            }
            else
            {
                txf1.setFocus(false);
                frameRate(60); // switch back to normal rate before stopping loop
                noLoop();
            }
        }
    }

    public void fileSelected(File selection)
    {
        if (selection == null)
        {
//    println("Window was closed or the user hit cancel.");
            Saving = 0; // false;
        }
        else if (Saving == 2) // 2 = DELETION
        {
            File selectedFile = new File(selection.getAbsolutePath());
            if (selectedFile.exists())
            {
                int answer = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to delete the following selected file?\n" + selection.getName(),
                        "File Deletion",
                        JOptionPane.YES_NO_OPTION);
                if (answer == 0) // if yes
                {
                    selectedFile.delete();
                    JOptionPane.showMessageDialog(frame, "The specified file has been deleted.");
                }
                else JOptionPane.showMessageDialog(frame, "Deletion has been cancelled.");
            }
            else
            {
                JOptionPane.showMessageDialog(frame, "Cannot find the specified file.");
            }
            Saving = 0;
        }
        else if (Saving == 1) // 1 = SAVING
        {
            PrintWriter output;
            String selectedFile = selection.getAbsolutePath().substring(selection.getAbsolutePath().length() - 4); // find last 4 characters of file name
            if (selectedFile.equals(".awv") || selectedFile.equals(".AWV")) selectedFile = selection.getAbsolutePath();                           // if file name includes ".awv" or ".AWV"
            else selectedFile = selection.getAbsolutePath() + ".awv";                                                                      // if name doesn't include ".awv" or ".AWV" add it to file name
            println("Saving to file: " + selectedFile);
            output = createWriter(selectedFile);
            for(int i = 0; i < ArbitraryPointNumber; i++)
            {
                output.println(ArbitraryWaveStep[i] + "\t" + ArbitraryWave[i]);
                //     println(ArbitraryWaveStep[i] + "\t" + ArbitraryWave[i]);
            }
            output.flush(); // Write the remaining data
            output.close(); // Finish the file
            if (selection.getName().length() > 4) // if name is long enough to include an extension
            {
                selectedFile = selection.getName().substring(selection.getName().length() - 4); // find last 4 characters of file name
                if (selectedFile.equals(".awv") || selectedFile.equals(".AWV")) OpenedWave = selection.getName();           // if file name includes ".awv" or ".AWV"
                else OpenedWave = selection.getName() + ".awv";                                                           // if name doesn't include ".awv" or ".AWV" add it to file name
            }
            else OpenedWave = selection.getName() + ".awv";                                                        // if name too short to include ".awv" or ".AWV" add it to file name
            Saving = 0; // false;
        }
        else if (Saving == 0) // 0 = OPENING
        {
            String line = "";
            //   println("--readfile--");
            try
            {
                int i = 0;
                Reader = new BufferedReader (new FileReader (selection.getAbsolutePath()));
                while ((line = Reader.readLine()) != null)
                {
                    String[] m1 = match(line, "\t"); // look for a tab in the line
                    if (m1 != null) // if line has a tab
                    {
                        String[] linePart = split(line, "\t");
                        ArbitraryWaveStep[i] = PApplet.parseInt(linePart[0]);
                        ArbitraryWave[i] = PApplet.parseInt(linePart[1]);
                    }
                    else // if line has no tab
                    {
                        TempX = -1; // signals a corrupted file
                        break;
                    }
                    i++;
                    if (i >= 4096) break; // largest number allowed
                }
                if (i > 0) ArbitraryPointNumber = i; // counted wave points, 1 extra point after end of wave
                OpenedWave = selection.getName();
            }
            catch (FileNotFoundException e)
            {
                println("File not found!");
                //     e.printStackTrace();
            }
            catch (IOException e)
            {
                println("Unknown error!");
                e.printStackTrace();
            }
            finally
            {
                if (Reader != null)
                {
                    try {Reader.close();}
                    catch (Exception e) {e.printStackTrace();}
                }
            }
            DrawnArbitrarySetup = 0; // redraw markers & clear drawing area
            DrawWave = 1; // draw wave
        } // end of if opening
        mousePressed = false;
    }

    //              fscale function: (Maps floats & adjusts linearity)
    public float fscale(float originalMin, float originalMax, float newBegin, float newEnd, float inputValue, float curve)
    {
        float originalRange = 0;
        float newRange = 0;
        float zeroRefCurVal = 0;
        float normalizedCurVal = 0;
        float rangedValue = 0;
        curve = pow(10, curve); // converts linear scale into lograthimic exponent for other pow function
        originalRange = originalMax - originalMin; // Zero Reference the values
        newRange = newEnd - newBegin;
        zeroRefCurVal = inputValue - originalMin;
        normalizedCurVal = zeroRefCurVal / originalRange; // normalize to 0 - 1 float
        rangedValue = (pow(normalizedCurVal, curve) * newRange) + newBegin;
        return rangedValue;
    }
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "DueAWGControllerLinux" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}
