/* 
Copyright Paul James Mutton, 2001-2007, http://www.jibble.org/

This file is part of PircBot.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

*/

package org.jibble.pircbot;

import java.util.*;

/**
 * This class is used to represent a user on an IRC server.
 * Instances of this class are returned by the getUsers method
 * in the PircBot class.
 *  <p>
 * Note that this class no longer implements the Comparable interface
 * for Java 1.1 compatibility reasons.
 *
 * @since   1.0.0
 * @author  Paul James Mutton,
 *          <a href="http://www.jibble.org/">http://www.jibble.org/</a>
 * @version    1.4.6 (Build time: Wed Apr 11 19:20:59 2007)
 */
public class User {

	public enum Prefix {
		NONE(null),
		VOICE('+'),
		HALFOP('%'),
		OP('@'),
		ADMIN('&'),
		OWNER('~');

		private Character symbol;
		Prefix(final Character symbol) {
			this.symbol = symbol;
		}

		@Override
		public String toString() {
			return String.valueOf(symbol);
		}
	}
	private List<Prefix> prefixes;
	private String _nick;
	private String _lowerNick;
	private String _login;
	private String _hostname;
    /**
     * Constructs a User object with a known prefix and nick.
     *
     * @param prefix The status of the user, for example, "@".
     * @param nick The nick of the user.
	 * @deprecated
     */
	public User(Prefix prefix, String nick) {
		prefixes = new ArrayList<Prefix>(2);
		if (prefix != null) prefixes.add(prefix);
		_nick = nick;
		_lowerNick = nick.toLowerCase();
	}
	public User(Prefix prefix, String nick, String login, String hostname) {
		prefixes = new ArrayList<Prefix>(2);
		if (prefix != null) prefixes.add(prefix);
		_nick = nick;
		_lowerNick = nick.toLowerCase();
		_login = login;
		_hostname = hostname;
	}
	public User(List<Prefix> prefix, String nick, String login, String hostname) {
		prefixes = prefix;
		_nick = nick;
		_lowerNick = nick.toLowerCase();
		_login = login;
		_hostname = hostname;
	}

    
    /**
     * Returns the prefix of the user. If the User object has been obtained
     * from a list of users in a channel, then this will reflect the user's
     * status in that channel.
     *
     * @return The prefix of the user. If there is no prefix, then an empty
     *         String is returned.
     */
    public List<Prefix> getPrefix() {
		//return new ArrayList<Prefix>(prefixes);
		return prefixes;
    }

	public void addPrefix(Prefix add) {
		if (add == Prefix.NONE) {
			prefixes.clear();
		} else {
			prefixes.remove(Prefix.NONE);
			prefixes.add(add);
		}
	}
	public void removePrefix(Prefix remove) {
		prefixes.remove(remove);
	}


	/**
     * Returns whether or not the user represented by this object is an
     * operator. If the User object has been obtained from a list of users
     * in a channel, then this will reflect the user's operator status in
     * that channel.
     * 
     * @return true if the user is an operator in the channel.
     */
	public boolean isOp() {
		return prefixes.contains(Prefix.OP);
	}
	public boolean isHalfOp() {
		return prefixes.contains(Prefix.HALFOP);
	}
	public boolean isOwner() {
		return prefixes.contains(Prefix.OWNER);
	}
	public boolean isAdmin() {
		return prefixes.contains(Prefix.ADMIN);
	}
	public boolean isVoice() {
		return prefixes.contains(Prefix.VOICE);
	}

    
    /**
     * Returns whether or not the user represented by this object has
     * voice. If the User object has been obtained from a list of users
     * in a channel, then this will reflect the user's voice status in
     * that channel.
     * 
     * @return true if the user has voice in the channel.
     */
    public boolean hasVoice() {
        return isVoice();
    }        
    
    
    /**
     * Returns the nick of the user.
     * 
     * @return The user's nick.
     */
    public String getNick() {
        return _nick;
    }
	public void setNick(String nick) {
		_nick = nick;
		_lowerNick = nick.toLowerCase();
	}


	/**
     * Returns the nick of the user complete with their prefix if they
     * have one, e.g. "@Dave".
     * 
     * @return The user's prefix and nick.
     */
    public String toString() {
		StringBuilder sb = new StringBuilder(40);
		for (Prefix pp : getPrefix()) {
			if (pp != Prefix.NONE) sb.append(pp);
		}
		sb.append(_nick);
		return sb.toString();
	}
    
    
    /**
     * Returns true if the nick represented by this User object is the same
     * as the argument. A case insensitive comparison is made.
     * 
     * @return true if the nicks are identical (case insensitive).
     */
    public boolean equals(String nick) {
        return nick.toLowerCase().equals(_lowerNick);
    }
    
    
    /**
     * Returns true if the nick represented by this User object is the same
     * as the nick of the User object given as an argument.
     * A case insensitive comparison is made.
     * 
     * @return true if o is a User object with a matching lowercase nick.
     */
    public boolean equals(Object o) {
        if (o instanceof User) {
            User other = (User) o;
            return other._lowerNick.equals(_lowerNick);
        }
        return false;
    }
    
    
    /**
     * Returns the hash code of this User object.
     * 
     * @return the hash code of the User object.
     */
    public int hashCode() {
        return _lowerNick.hashCode();
    }
    
    
    /**
     * Returns the result of calling the compareTo method on lowercased
     * nicks. This is useful for sorting lists of User objects.
     * 
     * @return the result of calling compareTo on lowercased nicks.
     */
    public int compareTo(Object o) {
        if (o instanceof User) {
            User other = (User) o;
            return other._lowerNick.compareTo(_lowerNick);
        }
        return -1;
    }

	public String getLogin() {
		return _login;
	}

	public String getHostname() {
		return _hostname;
	}
}
