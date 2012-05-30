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
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author Nicola Senno
 */
public class MeteogramFragmentAdapter extends FragmentPagerAdapter {

	private int mCount;
	protected String[] municipalityid;
	public MeteogramFragmentAdapter(FragmentManager fm, Context context) {
		super(fm);
		municipalityid = Global.istance().getPrefMunicpalityIds();
		
		// try to reload if municipality length is 0
		if(municipalityid.length < 1)
			municipalityid  = reload(context);
		
		mCount = municipalityid.length;
	}

	@Override
	public Fragment getItem(int position) {
		return MeteogramFragment.newInstance(municipalityid[position]);
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
	private String[] reload(Context context){
		Util.reloadAll(context);
		
		return  Global.istance().getPrefMunicpalityIds();
	}
}