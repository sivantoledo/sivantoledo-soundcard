package sivantoledo.soundcard;

public class Beep implements SoundcardProducer {
	
	private int rate;
	private double frequency;
	private double duration;
	private double volume;
	private double skip;
	private double f;
	
	private double t = 0;
	
	float[] buffer = new float[1000];
	
	public Beep(int rate) {
		this.rate = rate;
	}
	
	public void set(double frequency, double duration, double volume /* in dB */) {
		this.frequency = frequency;
		this.duration = duration;
		this.volume = volume;
		this.skip = 0;
		t = 0;
	}
	
	public void set(double frequency, double duration, double volume /* in dB */, double skip /* for two beeps */) {
		this.frequency = frequency;
		this.duration = duration;
		this.volume = volume;
		this.skip   = skip;
		t = 0;
	}

	@Override
	public float[] getTxSamplesBuffer() {
		return buffer;
	}

	double shape = 500; // top frequency in Hz (bandwidth limit)	
	
	@Override
	public int getSamples() {
		int i = 0;
		double volumefactor = Math.pow(10, volume/20.0);
		double fstep = frequency / (rate*duration);

		if (t==0) f = frequency; // at the beginning of the beep, we set the local frequency f; may go up during beep

		while (t < duration && i < buffer.length) {
			double shapingfactor = 1.0;
			if (t < 0.25/shape)
				shapingfactor = Math.sin( 2 * Math.PI * shape * t );
			if (t-duration > -0.25/shape)
				shapingfactor = - Math.sin( 2 * Math.PI * shape * (t-duration) );
			//System.out.printf("%f\n", shapingfactor);
		
			buffer[i] = (float) (shapingfactor * volumefactor * Math.sin( 2 * Math.PI * f * t ));
			if (skip > 0 && (t > (duration-skip)/2) && (t < skip+(duration-skip)/2)) buffer[i] = 0;
			if (skip < 0) f += fstep;

			t += (1/(double)rate);
			i++;
		}
		//System.err.printf("Beep.getSamples %d volume %f\n",i,volumefactor);
		return i;
	}

}
