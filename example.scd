MIDIClient.init;
MIDIClient.destinations;
MIDIIn.connectAll;

~irigpads = IRigPads();

~irigpads.pad1.onChange = {|val|
	if( val>0, {
		("Pad1 pressed").postln;
	}, {});
};