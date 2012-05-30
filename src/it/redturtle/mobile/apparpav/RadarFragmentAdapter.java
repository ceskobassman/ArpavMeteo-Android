/*
 * Apparpav is copyright of Agenzia Regionale per la Prevenzione e
 * Protezione Ambientale del Veneto - Via Matteotti, 27 - 35137
 * Padova Italy
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA.
 */

package it.redturtle.mobile.apparpav;

import it.redturtle.mobile.apparpav.utils.Global;
import it.redturtle.mobile.apparpav.utils.Util;

import java.util.LinkedList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author Nicola Senno
 */
public class RadarFragmentAdapter extends FragmentPagerAdapter {

	private int mCount;
	protected LinkedList<Radar> radars;
	protected Context context;

	public RadarFragmentAdapter(FragmentManager fm, Context c) {
		super(fm);
		radars = Global.istance().getRadars();
		context = c;
		// try to reload if municipality length is 0
		if(radars.size() < 1)
			radars  = reload(context);

		mCount = radars.size();
	}

	@Override
	public Fragment getItem(int position) {
		return RadarFragment.newInstance(radars.get(position));
	}

	@Override
	public int getCount() {
		return mCount;
	}

	public void setCount(int count) {
		if (count > 0 && count <= 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}

	/** 
	 * Reload Global in case of null pointer
	 * @param context
	 * @return
	 */
	private LinkedList<Radar> reload(Context context){
		Util.reloadAll(context);
		return  Global.istance().getRadars();
	}
}