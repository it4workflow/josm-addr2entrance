package org.openstreetmap.josm.plugins.addr2entrance;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class Address {

	public static final String KEY_ADDR_COUNTRY = "addr:country";
	public static final String KEY_ADDR_STATE = "addr:state";
	public static final String KEY_ADDR_CITY = "addr:city";
	public static final String KEY_ADDR_POSTCODE = "addr:postcode";
	public static final String KEY_ADDR_STREET = "addr:street";
	public static final String KEY_ADDR_PLACE = "addr:place";
	public static final String KEY_ADDR_HOUSENUMBER = "addr:housenumber";

	private String country;
	private String state;
	private String city;
	private String postcode;
	private String street;
	private String place;
	private String housenumber;

	private OsmPrimitive primitive;

	// ######################################################################

	public Address(OsmPrimitive primitive) {

		this.primitive = primitive;

		country = primitive.get(KEY_ADDR_COUNTRY);
		state = primitive.get(KEY_ADDR_STATE);
		city = primitive.get(KEY_ADDR_CITY);
		postcode = primitive.get(KEY_ADDR_POSTCODE);
		street = primitive.get(KEY_ADDR_STREET);
		place = primitive.get(KEY_ADDR_PLACE);
		housenumber = primitive.get(KEY_ADDR_HOUSENUMBER);
	}

	// ######################################################################

	public boolean hasAddress() {

		boolean hasAddress = false;
		for (String key : primitive.getKeys().keySet()) {
			if (key.startsWith("addr:") && !"addr:flats".equals(key)) {
				hasAddress = true;
				break;
			}
		}
		return hasAddress;
	}

	// ######################################################################

	public OsmPrimitive getPrimitive() {

		return primitive;
	}

	public void setPrimitive(OsmPrimitive primitive) {

		this.primitive = primitive;
	}

	public String getCountry() {

		return country;
	}

	public void setCountry(String country) {

		this.country = country;
	}

	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	public String getCity() {

		return city;
	}

	public void setCity(String city) {

		this.city = city;
	}

	public String getPostcode() {

		return postcode;
	}

	public void setPostcode(String postcode) {

		this.postcode = postcode;
	}

	public String getStreet() {

		return street;
	}

	public void setStreet(String street) {

		this.street = street;
	}

	public String getPlace() {

		return place;
	}

	public void setPlace(String place) {

		this.place = place;
	}

	public String getHousenumber() {

		return housenumber;
	}

	public void setHousenumber(String housenumber) {

		this.housenumber = housenumber;
	}

	// ######################################################################

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((city == null) ? 0 : city.hashCode());
		result = (prime * result)
				+ ((country == null) ? 0 : country.hashCode());
		result = (prime * result)
				+ ((housenumber == null) ? 0 : housenumber.hashCode());
		result = (prime * result) + ((place == null) ? 0 : place.hashCode());
		result = (prime * result)
				+ ((postcode == null) ? 0 : postcode.hashCode());
		result = (prime * result) + ((state == null) ? 0 : state.hashCode());
		result = (prime * result) + ((street == null) ? 0 : street.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Address other = (Address) obj;
		if (city == null) {
			if (other.city != null) {
				return false;
			}
		} else if (!city.equals(other.city)) {
			return false;
		}
		if (country == null) {
			if (other.country != null) {
				return false;
			}
		} else if (!country.equals(other.country)) {
			return false;
		}
		if (housenumber == null) {
			if (other.housenumber != null) {
				return false;
			}
		} else if (!housenumber.equals(other.housenumber)) {
			return false;
		}
		if (place == null) {
			if (other.place != null) {
				return false;
			}
		} else if (!place.equals(other.place)) {
			return false;
		}
		if (postcode == null) {
			if (other.postcode != null) {
				return false;
			}
		} else if (!postcode.equals(other.postcode)) {
			return false;
		}
		if (state == null) {
			if (other.state != null) {
				return false;
			}
		} else if (!state.equals(other.state)) {
			return false;
		}
		if (street == null) {
			if (other.street != null) {
				return false;
			}
		} else if (!street.equals(other.street)) {
			return false;
		}
		return true;
	}

	// ######################################################################

	@Override
	public String toString() {

		return "Address [country=" + country + ", state=" + state + ", city="
				+ city + ", postcode=" + postcode + ", street=" + street
				+ ", place=" + place + ", housenumber=" + housenumber + "]";
	}

}
