package uk.co.unclealex.callerid.server.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import uk.co.unclealex.callerid.shared.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.shared.model.CountryAndArea;
import uk.co.unclealex.callerid.shared.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.shared.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.shared.model.PhoneNumber;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class NumberLocationServiceImpl implements NumberLocationService {

	public static final String BASINGSTOKE = "1256";
	public static final String UK = "44";
	
	private Map<String, Map<String, CountryAndArea>> i_countryAndAreasByAreaCodeByCountryCode;
	private Map<String, Set<String>> i_countriesByCountryCode;
	
	@PostConstruct
	public void initialise() throws IOException {
		Comparator<String> longestFirstComparator = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int cmp = o2.length() - o1.length();
				return cmp == 0?o1.compareTo(o2):cmp;
			}
		};
		Map<String, Map<String, CountryAndArea>> countryAndAreasByAreaCodeByCountryCode = 
				Maps.newTreeMap(longestFirstComparator);
		Map<String, Set<String>> unsortedCountriesByCountryCode = 
				Maps.newTreeMap(longestFirstComparator);
		
		Function<String, String> noQuotesFunction = createPatternFunction("\"(.+)\"", 1);
		
		InputStream in = getClass().getClassLoader().getResourceAsStream("globalareacodes.csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
		// Ignore the header line.
		String line = reader.readLine();
		Splitter commaSplitter = Splitter.on(',').omitEmptyStrings().trimResults();
		while ((line = reader.readLine()) != null) {
			Iterable<String> cells = Iterables.transform(commaSplitter.split(line), noQuotesFunction);
			Iterator<String> cellIterator = cells.iterator();
			String country = cellIterator.next();
			String countryCode = cellIterator.next();
			if (!cellIterator.hasNext()) {
				Set<String> countries = unsortedCountriesByCountryCode.get(countryCode);
				if (countries == null) {
					countries = Sets.newHashSet();
					unsortedCountriesByCountryCode.put(countryCode, countries);
				}
				countries.add(country);
			}
			else {
				String area = cellIterator.next();
				String areaCode = trailingNumbers(cellIterator.next());
				Map<String, CountryAndArea> countryAndAreasByAreaCode = countryAndAreasByAreaCodeByCountryCode.get(countryCode);
				if (countryAndAreasByAreaCode == null) {
					countryAndAreasByAreaCode = Maps.newTreeMap(longestFirstComparator);
					countryAndAreasByAreaCodeByCountryCode.put(countryCode, countryAndAreasByAreaCode);
				}
				countryAndAreasByAreaCode.put(areaCode, new CountryAndArea(country, area));
			}
		}
		reader.close();

		final Map<String, Integer> countryCounts = Maps.newHashMap();
		for (Map<String, CountryAndArea> map : countryAndAreasByAreaCodeByCountryCode.values()) {
			for (CountryAndArea countryAndArea : map.values()) {
				String country = countryAndArea.getCountry();
				Integer countryCount = countryCounts.get(country);
				if (countryCount == null) {
					countryCounts.put(country, 1);
				}
				else {
					countryCounts.put(country, countryCount + 1);
				}
			}
		}
		final Comparator<String> mostPopulousFirstComparator = new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				int cmp = count(s2) - count(s1);
				return cmp == 0?s1.compareTo(s2):cmp;
			}

			public int count(String country) {
				Integer count = countryCounts.get(country);
				return count == null?0:count;
			}
		};
		Function<Set<String>, Set<String>> countrySearchingFunction = new Function<Set<String>, Set<String>>() {
			@Override
			public Set<String> apply(Set<String> unsortedCountries) {
				Set<String> sortedCountries = Sets.newTreeSet(mostPopulousFirstComparator);
				sortedCountries.addAll(unsortedCountries);
				return sortedCountries;
			}
		};
		setCountriesByCountryCode(Maps.transformValues(unsortedCountriesByCountryCode, countrySearchingFunction));
		setCountryAndAreasByAreaCodeByCountryCode(countryAndAreasByAreaCodeByCountryCode);
	}
	
	protected String trailingNumbers(String str) {
		Integer lastNumberIdx = null;
		for (int idx = str.length() - 1; lastNumberIdx == null && idx >= 0; idx--) {
			if (!Character.isDigit(str.charAt(idx))) {
				lastNumberIdx = idx;
			}
		}
		return lastNumberIdx == null?str:str.substring(lastNumberIdx + 1);
	}

	protected Function<String, String> createPatternFunction(String regex, final int replacement) {
		final Pattern p = Pattern.compile(regex);
		return new Function<String, String>() {
			@Override
			public String apply(String input) {
				Matcher m = p.matcher(input);
				if (m.matches()) {
					return m.group(replacement);
				}
				else {
					return input;
				}
			}
		};
	}
	
	@Override
	public PhoneNumber decomposeNumber(String number) {
		number = normaliseNumber(number);
		Entry<String, Map<String, CountryAndArea>> countryEntry = 
				findEntry(number, getCountryAndAreasByAreaCodeByCountryCode().entrySet());
		if (countryEntry == null) {
			return new NumberOnlyPhoneNumber(number);
		}
		String countryCode = countryEntry.getKey();
		number = number.substring(countryCode.length());
		Map<String, CountryAndArea> countryAndAreasByAreaCode = countryEntry.getValue();
		Entry<String, CountryAndArea> areaEntry = findEntry(number, countryAndAreasByAreaCode.entrySet());
		if (areaEntry == null) {
			Set<String> countries = getCountriesByCountryCode().get(countryCode);
			return new CountriesOnlyPhoneNumber(countries, countryCode, number);
		}
		else {
			String areaCode = areaEntry.getKey();
			CountryAndArea countryAndArea = areaEntry.getValue();
			number = number.substring(areaCode.length());
			return new CountryAndAreaPhoneNumber(countryAndArea.getCountry(), countryAndArea.getArea(), countryCode, areaCode, number);
		}
	}
	
	@Override
	public String normaliseNumber(String number) {
		if (number.startsWith("+")) {
			number = number.substring(1);
		}
		else if (!number.startsWith("0")) {
			number = UK + BASINGSTOKE + number;
		}
		else if (!number.startsWith("00")) {
			number = UK + number.substring(1);
		}
		else {
			number = number.substring(2);
		}
		return number;
	}
	
	protected <T> Entry<String, T> findEntry(final String number, Set<Entry<String, T>> entrySet) {
		Predicate<Entry<String, T>> predicate = new Predicate<Entry<String,T>>() {
			@Override
			public boolean apply(Entry<String, T> entry) {
				return number.startsWith(entry.getKey());
			}
		};
		return Iterables.find(entrySet, predicate, null);
	}
	
	public Map<String, Map<String, CountryAndArea>> getCountryAndAreasByAreaCodeByCountryCode() {
		return i_countryAndAreasByAreaCodeByCountryCode;
	}

	public void setCountryAndAreasByAreaCodeByCountryCode(
			Map<String, Map<String, CountryAndArea>> countryAndAreaByAreaCodeByCountryCode) {
		i_countryAndAreasByAreaCodeByCountryCode = countryAndAreaByAreaCodeByCountryCode;
	}

	public Map<String, Set<String>> getCountriesByCountryCode() {
		return i_countriesByCountryCode;
	}

	public void setCountriesByCountryCode(Map<String, Set<String>> countriesByCountryCode) {
		i_countriesByCountryCode = countriesByCountryCode;
	}
}
