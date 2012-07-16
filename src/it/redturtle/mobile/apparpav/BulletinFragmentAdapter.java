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

import it.redturtle.mobile.apparpav.types.Forecast;
import it.redturtle.mobile.apparpav.utils.Global;
import it.redturtle.mobile.apparpav.utils.Util;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * @author Nicola Senno
 */
public class BulletinFragmentAdapter extends FragmentPagerAdapter {

	protected List<Forecast> forecasts;
	private Activity activity;
	private int mCount;
	private String currentBulletinid;

	public BulletinFragmentAdapter(FragmentManager fm, Activity activity, String bulletinid) {
		super(fm);
		this.activity = activity;
		currentBulletinid = bulletinid;
		forecasts = Global.istance().getForecastByBulletinID(bulletinid);
		
		if(null == forecasts)
			forecasts = reload(activity, bulletinid);

		mCount = forecasts.size();
	}

	@Override
	public Fragment getItem(int position) {
		Log.d("YourTag", "getItem");
		Forecast f = forecasts.get(position);
		return ForecastFragment.newInstance(activity,f, currentBulletinid);
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
	
	private List<Forecast> reload(Context context, String bulletinid ){
		Util.reloadAll(context);
		
		return Global.istance().getForecastByBulletinID(bulletinid);
	}
}