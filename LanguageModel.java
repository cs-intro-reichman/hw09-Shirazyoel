import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		// Your code goes here
        String window = "";
        char c;
        In in = new In(fileName); 

    for (int i = 0; i < windowLength; i++) {
        if (!in.isEmpty()) {
            window += in.readChar();
        }
    }
    while (!in.isEmpty()) {
        c = in.readChar();
        List probs = CharDataMap.get(window);
        if (probs == null) {
            probs = new List();
            CharDataMap.put(window, probs);
        }
        probs.update(c);

     window = window.substring(1) + c;
    }
    for (List probs : CharDataMap.values()) {
        calculateProbabilities(probs);
    }
}
        
    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	void calculateProbabilities(List probs) {				
		// Your code goes here
     
    int total = 0;
    for (int i = 0; i < probs.getSize(); i++) {
        total = total + probs.get(i).count;
    }

    double cumulativeProb = 0.0;
    for (int i = 0; i < probs.getSize(); i++) {
        CharData current = probs.get(i);
        current.p = (double) current.count / total;
        cumulativeProb = cumulativeProb + current.p;
        current.cp = cumulativeProb;
    }
}

    // Returns a random character from the given probabilities list.
	char getRandomChar(List probs) {
		// Your code goes here
        // Returns a random character from the given probabilities list.
        double r = randomGenerator.nextDouble();
        for (int i = 0; i < probs.getSize(); i++) {
            CharData current = probs.get(i);
            if (current.cp > r) {
                return current.chr;
            }
        }
        return probs.get(probs.getSize() - 1).chr;
    }
		
	

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		// Your code goes here

    // 1. בדיקה אם ה"פרומפט" ארוך מספיק כדי להתחיל את התהליך [cite: 227]
    if (initialText.length() < windowLength) {
        return initialText; // אם לא, מחזירים את הטקסט המקורי ומפסיקים [cite: 228]
    }

    // 2. אתחול משתנה הטקסט שנוצר והחלון הנוכחי [cite: 230]
    String generatedText = initialText;
    // החלון מוגדר כ-n התווים האחרונים של הפרומפט [cite: 230]
    String window = initialText.substring(initialText.length() - windowLength);

    // 3. לולאת יצירת הטקסט עד הגעה לאורך המבוקש 
    while (generatedText.length() < textLength) {
        // ניסיון לשלוף את רשימת האפשרויות עבור החלון הנוכחי מהמפה [cite: 217]
        List probs = (List) probabilities.get(window);

        // אם החלון לא נמצא במפה, עוצרים ומחזירים את מה שנוצר עד כה 
        if (probs == null) {
            break;
        }

        // 4. הגרלת תו חדש מתוך הרשימה בעזרת שיטת מונטה קרלו [cite: 219]
        char nextChar = getRandomChar(probs);

        // 5. עדכון הטקסט הנוצר והחלון [cite: 220, 231]
        generatedText += nextChar;
        // החלון החדש הוא תמיד windowLength התווים האחרונים של מה שנוצר 
        window = generatedText.substring(generatedText.length() - windowLength);
    }

    return generatedText;
}

    

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		// Your code goes here
    }
}
