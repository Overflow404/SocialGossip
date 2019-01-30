import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

public final class IsoUtil {
	/**
	 * Overview:	Classe di utility per fare check sui linguaggi e nazionalita' degli utenti.
	 */
	private static final Set<String> ISO_LANGUAGES = new HashSet<String>(Arrays.asList(Locale.getISOLanguages()));
	private static final Set<String> ISO_COUNTRIES = new HashSet<String>(Arrays.asList(Locale.getISOCountries()));

	private IsoUtil() {}

	/**
	 * @param 	s
	 * @return	True se s appartiene ai linguaggi iso, false altrimenti.
	 */
	public static boolean isValidISOLanguage(String s) {
		return ISO_COUNTRIES.contains(s);
	}

	/**
	 * @param 	s
	 * @return	True se s appartiene ai paesi iso, false altrimenti.
	 */
	public static boolean isValidISOCountry(String s) {
		return ISO_LANGUAGES.contains(s);
	}
}