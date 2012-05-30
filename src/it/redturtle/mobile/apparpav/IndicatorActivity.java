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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.viewpagerindicator.PageIndicator;

/**
 * @author Nicola Senno
 */
public abstract class IndicatorActivity extends FragmentActivity {
	public FragmentPagerAdapter mAdapter;
	public ViewPager mPager;
	public PageIndicator mIndicator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent newintent = null;
		 switch (item.getItemId()) {
		  case R.id.pref:
				newintent = new Intent(this.getApplicationContext(), ConfActivity.class);
				newintent.putExtra("reload", true);
				startActivity(newintent);
		        return true;
		    case R.id.update:
				newintent = new Intent(this.getApplicationContext(), MeteogramsActivity.class);
				newintent.putExtra("reload", true);
				startActivity(newintent);
		        return true;
		    }

		return super.onOptionsItemSelected(item);
	}
}
