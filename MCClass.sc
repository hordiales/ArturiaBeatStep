// =======================================================
// Title         :  iRig PADS, MPK mini, AkaiMidiMix, etc based on ArturiaBeatStep (by David Granström 2015)
// Description   : Controller class for iRig PADS
// Version       :
// Copyright (c) : basado en código de "https://github.com/davidgranstrom/ArturiaBeatStep"
// =======================================================

AkaiMidiMix {
	var <knobs, <recpads, <mutepads;

    var ctls;
    var knobValues, muteValues, recValues, extraValues;

    *new {
        ^super.new.init;
    }

    init {
        ctls = ();

        knobs = List[];
		mutepads  = List[];
        recpads  = List[];

        //Knobs: Row 1,2,3
		//Slides: Row 4
		//master slide: 62
	    knobValues = [
			16,20,24,28,46,50,54,58,
			17,21,25,29,47,51,55,59,
			18,22,26,30,48,52,56,60,
			19,23,27,31,49,53,57,61,
			62
		];

		/*
		// Row1
		// Row2
		// Col1
        padValues  = [
			1,4,7,10,13,16,19,22,
			3,6,9,12,15,18,21,24,
			25,26,27
		];*/

		muteValues  = [
			1,4,7,10,13,16,19,22
		];
		recValues  = [
			3,6,9,12,15,18,21,24
		];

	    //bank left, right and mute
		extraValues = [
			25,26,27
		];

        MIDIClient.init;
        MIDIIn.connectAll;

        this.assignCtls;
    }

    assignCtls {
        knobValues.do {|cc, i|
            var key  = ("knob" ++ (i+1)).asSymbol;
            var knob = ABSKnob(key, cc);
            knobs.add(knob);
            ctls.put(key, knob);
        };

        muteValues.collect {|note, i|
            var key = ("mutepad" ++ (i+1)).asSymbol;
            var pad = ABSPad(key, note);
            mutepads.add(pad);
            ctls.put(key, pad);
        };

		recValues.collect {|note, i|
            var key = ("recpad" ++ (i+1)).asSymbol;
            var pad = ABSPad(key, note);
            recpads.add(pad);
            ctls.put(key, pad);
        };
    }

    freeAll {
        ctls.do(_.free);
    }

    doesNotUnderstand {|selector ... args|
        ^ctls[selector] ?? { ^super.doesNotUnderstand(selector, args) }
    }
}

AkaiMPKMini {
var <knobs, <pads;

    var ctls;
    var knobValues, padValues;

    *new {
        ^super.new.init;
    }

    init {
        ctls = ();

        knobs = List[];
        pads  = List[];

        //                    Row1             Row2
	    knobValues = [ 7, 10, 8, 1,   12, 13, 11, 33];

		//                    Bank1 Row1      Bank1 Row2      Bank2 Row1       Bank2 Row2
        padValues  = [ 48, 49, 50, 51,  44, 45, 46, 19,  36, 37, 38, 39,  32,33,34,35  ];

        MIDIClient.init;
        MIDIIn.connectAll;

        this.assignCtls;
    }

    assignCtls {
        knobValues.do {|cc, i|
            var key  = ("knob" ++ (i+1)).asSymbol;
            var knob = ABSKnob(key, cc);
            knobs.add(knob);
            ctls.put(key, knob);
        };

        padValues.collect {|note, i|
            var key = ("pad" ++ (i+1)).asSymbol;
            var pad = ABSPad(key, note);
            pads.add(pad);
            ctls.put(key, pad);
        };
    }

    freeAll {
        ctls.do(_.free);
    }

    doesNotUnderstand {|selector ... args|
        ^ctls[selector] ?? { ^super.doesNotUnderstand(selector, args) }
    }
}

IRigPads {
    var <knobs, <pads;

    var ctls;
    var knobValues, padValues;

    *new {
        ^super.new.init;
    }

    init {
        ctls = ();

        knobs = List[];
        pads  = List[];

        knobValues = [ 10, 11, 1, 7 ];
        padValues  = [ 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63];

        MIDIClient.init;
        MIDIIn.connectAll;

        this.assignCtls;
    }

    assignCtls {
        knobValues.do {|cc, i|
            var key  = ("knob" ++ (i+1)).asSymbol;
            var knob = ABSKnob(key, cc);
            knobs.add(knob);
            ctls.put(key, knob);
        };

        padValues.collect {|note, i|
            var key = ("pad" ++ (i+1)).asSymbol;
            var pad = ABSPad(key, note);
            pads.add(pad);
            ctls.put(key, pad);
        };
    }

    freeAll {
        ctls.do(_.free);
    }

    doesNotUnderstand {|selector ... args|
        ^ctls[selector] ?? { ^super.doesNotUnderstand(selector, args) }
    }
}

ABSKnob {
    var key, cc;

    *new {|key, cc|
        ^super.newCopyArgs(("abs_" ++ key).asSymbol, cc);
    }

    onChange_ {|func|
        MIDIdef.cc(key, func, cc);
    }

    free {
        MIDIdef.cc(key).free;
    }
}

ABSPad {
    var key, <note; //note has a getter

    *new {|key, note|
        ^super.newCopyArgs("abs_" ++ key, note);
    }

    onPress_ {|func|
        MIDIdef.noteOn((key ++ "_on").asSymbol, {|val| func.(val) }, note);
    }

    onRelease_ {|func|
        MIDIdef.noteOff((key ++ "_off").asSymbol, {|val| func.(val) }, note);
    }

    onChange_ {|func|
        MIDIdef.noteOn((key ++ "_on_change").asSymbol, {|val| func.(val) }, note);
        MIDIdef.noteOff((key ++ "_off_change").asSymbol, {|val| func.(val) }, note);
    }

    free {
        var labels = [ "_on", "_off", "_on_change", "_off_change" ];

        labels.do {|label|
            var k = (key ++ label).asSymbol;
            MIDIdef.cc(k).free;
        };
    }
}
