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

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Nicola Senno
 */
public class ChoiceAdapter extends BaseAdapter {

	private Activity activity;
	private List<Map<String, Object>> elements;
	private static LayoutInflater inflater = null;
	private int state;


	public ChoiceAdapter(Activity a, List<Map<String, Object>> elements, int state) {
		activity = a;
		this.state = state;
		this.elements =  elements;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return elements.size();
	}

	public Object getItem(int position) {
		return elements.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder{
		public TextView text;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		int[] l = {0,1,R.layout.provinces_items, R.layout.municipality_items};
		if(convertView == null){
			convertView = inflater.inflate(l[state], null);
			holder= new ViewHolder();
			holder.text=(TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
			
		} else {
			holder=(ViewHolder)convertView.getTag();
		}

		holder.text.setText((String)elements.get(position).get("title"));
		return convertView;
	}

}
