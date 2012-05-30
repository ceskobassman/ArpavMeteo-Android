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

import it.redturtle.mobile.apparpav.types.Row;
import it.redturtle.mobile.apparpav.types.Slot;

import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

/**
 * @author Nicola Senno
 */
public class MeteogramAdapter extends BaseAdapter {

	private final int TITOLO 	= 0;
	private final int IMAGE 	= 1;
	private final int TEXT 		= 2;
		
	LinkedList<Slot> listOfSlot;
	Context context;
	private static LayoutInflater inflater = null;

	public MeteogramAdapter(Context context, LinkedList<Slot> listOfSlot) {
		this.listOfSlot =  listOfSlot;
		this.context = context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return listOfSlot.size();
	}

	public Object getItem(int position) {
		return listOfSlot.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		Slot slot = listOfSlot.get(position);
		convertView = inflater.inflate(R.layout.meteogram_layout, null);	
		LinearLayout linear = (LinearLayout)convertView.findViewById(R.id.meteogram);

		LinkedList<Row> rows = slot.getListOfRows();
		for(int i=0;i < rows.size(); i++)
			linear = getRow(rows.get(i), linear);


		return convertView;
	}

	private LinearLayout getRow(Row row, LinearLayout linear){
		if(row.getContentType() == TITOLO && row.getType() == 0 )
			return this.getSingleTitleRow(row.getMap(), linear);
		
		else if(row.getContentType() == TITOLO && row.getType() ==  1)
			return this.getDoubleTitleRow(row.getMap(), linear);
		
		else if(row.getContentType() == TEXT && row.getType() ==  0)
			return this.getSingleTextRow(row.getMap(), linear);

		else if(row.getContentType() == TEXT && row.getType() ==  1)
			return this.getDoubleTextRow(row.getMap(), linear);

		else if(row.getContentType() == IMAGE && row.getType() ==  0)
			return this.getSingleImageRow(row.getMap(), linear);

		else if(row.getContentType() == IMAGE && row.getType() ==  1)
			return this.getDoubleImageRow(row.getMap(), linear);
		
		return null;
	}

	/**
	 * SINGLE TITLE ROW
	 * @param att
	 * @param linear
	 * @return
	 */
	public LinearLayout getSingleTitleRow(Map<String, String> att, LinearLayout linear){
		LinearLayout container_layout = new LinearLayout(context);
		container_layout.setMinimumHeight(30);
		container_layout.setBackgroundDrawable(context.getResources().getDrawable( R.drawable.view_shape_meteo_blue));
		container_layout.setVerticalGravity(Gravity.CENTER);
		
		LinearLayout.LayoutParams value_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 0.5f);
		TextView tx = new TextView(context);
		tx.setTextColor(Color.rgb(66,66,66));
		container_layout.addView(tx, value_params);

		LinearLayout.LayoutParams ltext1 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 0.5f);
		TextView t1 = new TextView(context);
		t1.setText(att.get("value"));
		t1.setTextSize(11);
		t1.setGravity(Gravity.CENTER_HORIZONTAL);
		t1.setPadding(2, 0, 0, 2);
		t1.setTextColor(Color.rgb(255,255,255));
		container_layout.addView(t1,ltext1);
		
		linear.addView(container_layout);
		return linear;
	}
	
	/**
	 * DOUBLE TITLE ROW
	 * @param att
	 * @param linear
	 * @return
	 */
	public LinearLayout getDoubleTitleRow(Map<String, String> att, LinearLayout linear){
		LinearLayout container_layout = new LinearLayout(context);
		container_layout.setMinimumHeight(30);
		container_layout.setBackgroundDrawable(context.getResources().getDrawable( R.drawable.view_shape_meteo_blue));
		container_layout.setVerticalGravity(Gravity.CENTER);
		container_layout.setOrientation(LinearLayout.HORIZONTAL);

		LinearLayout.LayoutParams value_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 0.3f);
		TextView tx = new TextView(context);
		tx.setText("");
		tx.setTextColor(Color.rgb(66,66,66));
		container_layout.addView(tx, value_params);

		LinearLayout.LayoutParams ltext1 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 0.35f);
		TextView t1 = new TextView(context);
		t1.setText(att.get("value1"));
		t1.setTextSize(11);
		t1.setGravity(Gravity.CENTER_HORIZONTAL);
		t1.setPadding(2, 0, 0, 2);
		t1.setTextColor(Color.rgb(255,255,255));
		container_layout.addView(t1,ltext1);

		LinearLayout.LayoutParams ltext2 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 0.35f);
		TextView t2 = new TextView(context);
		t2.setText(att.get("value2"));
		t2.setTextSize(11);
		t2.setGravity(Gravity.CENTER_HORIZONTAL);
		t2.setPadding(2, 0, 0, 2);
		t2.setTextColor(Color.rgb(255,255,255));
		container_layout.addView(t2, ltext2);
		
		linear.addView(container_layout);
		return linear;
	}
	
	/**
	 * SINGLE IMAGE ROW
	 * @param att
	 * @param linear
	 * @return
	 */
	public LinearLayout getSingleImageRow(Map<String, String> att, LinearLayout linear){
		
		LinearLayout container_layout = new LinearLayout(context);
		container_layout.setBackgroundDrawable(context.getResources().getDrawable( R.drawable.view_shape_meteo));
		container_layout.setMinimumHeight(46);
		container_layout.setVerticalGravity(Gravity.CENTER);

		LinearLayout.LayoutParams value = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 0.5f);
		TextView tx = new TextView(context);
		tx.setText(att.get("title"));
		tx.setTextSize(11);
		tx.setTypeface(null, Typeface.BOLD);
		tx.setGravity(Gravity.LEFT);
		tx.setPadding(3, 0, 0, 2);
		tx.setTextColor(Color.rgb(66,66,66));
		container_layout.addView(tx, value);

		LinearLayout.LayoutParams value_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 40, 0.5f);
		ImageView img = new ImageView(context);
		String path = "it.redturtle.mobile.apparpav:drawable/"+FilenameUtils.removeExtension(att.get("value"));
		img.setImageResource(context.getResources().getIdentifier(path, null, null));
		img.setPadding(0, 3, 0, 3);
		container_layout.addView(img, value_params);
		
		linear.addView(container_layout);
		return linear;
	}

	/**
	 * DOUBLE IMAGE ROW
	 * @param att
	 * @param linear
	 * @return
	 */
	public LinearLayout getDoubleImageRow(Map<String, String> att, LinearLayout linear){
		
		LinearLayout container_layout = new LinearLayout(context);
		container_layout.setBackgroundDrawable(context.getResources().getDrawable( R.drawable.view_shape_meteo));
		container_layout.setMinimumHeight(46);
		container_layout.setVerticalGravity(Gravity.CENTER);

		LinearLayout.LayoutParams value_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 0.3f);
		TextView tx = new TextView(context);
		tx.setText(att.get("title"));
		tx.setTextSize(11);
		tx.setTypeface(null, Typeface.BOLD);
		tx.setGravity(Gravity.LEFT);
		tx.setPadding(3, 0, 0, 2);
		tx.setTextColor(Color.rgb(66,66,66));
		container_layout.addView(tx, value_params);

		LinearLayout.LayoutParams value_one_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,46, 0.35f);
		ImageView img1 = new ImageView(context);
		String path = "it.redturtle.mobile.apparpav:drawable/"+FilenameUtils.removeExtension(att.get("value1"));
		img1.setImageResource(context.getResources().getIdentifier(path, null, null));
		img1.setPadding(0, 3, 0, 3);
		container_layout.addView(img1, value_one_params);

		LinearLayout.LayoutParams value_two_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,46, 0.35f);
		ImageView img2 = new ImageView(context);
		path = "it.redturtle.mobile.apparpav:drawable/"+FilenameUtils.removeExtension(att.get("value2"));
		img2.setImageResource(context.getResources().getIdentifier(path, null, null));
		img2.setPadding(0, 3, 0, 3);
		container_layout.addView(img2, value_two_params);

		linear.addView(container_layout);
	
		return linear;
	}

	/**
	 * SINGLE TEXT ROW
	 * @param att
	 * @param linear
	 * @return
	 */
	public LinearLayout getSingleTextRow(Map<String, String> att, LinearLayout linear){
		
		LinearLayout container_layout = new LinearLayout(context);
		container_layout.setBackgroundDrawable(context.getResources().getDrawable( R.drawable.view_shape_meteo));
		container_layout.setMinimumHeight(46);
		container_layout.setVerticalGravity(Gravity.CENTER);

		LinearLayout.LayoutParams value = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 0.5f);
		TextView tx = new TextView(context);
		tx.setText(att.get("title"));
		tx.setTextSize(11);
		tx.setTypeface(null, Typeface.BOLD);
		tx.setGravity(Gravity.LEFT);
		tx.setPadding(3, 0, 0, 2);
		tx.setTextColor(Color.rgb(66,66,66));
		container_layout.addView(tx, value);

		LinearLayout.LayoutParams value_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 0.5f);
		TextView t2 = new TextView(context);
		t2.setText(att.get("value").equals("")?" - ":att.get("value"));
		t2.setTextSize(11);
		t2.setGravity(Gravity.CENTER_HORIZONTAL);
		t2.setPadding(2, 0, 0, 2);
		t2.setTextColor(Color.rgb(66,66,66));
		container_layout.addView(t2, value_params);

		linear.addView(container_layout);
		return linear;
	}

	/**
	 * DOUBLE TEXT ROW
	 * @param att
	 * @param linear
	 * @return
	 */
	public LinearLayout getDoubleTextRow(Map<String, String> att, LinearLayout linear){
		
		LinearLayout container_layout = new LinearLayout(context);
		container_layout.setBackgroundDrawable(context.getResources().getDrawable( R.drawable.view_shape_meteo));
		container_layout.setMinimumHeight(46);
		container_layout.setVerticalGravity(Gravity.CENTER);

		LinearLayout.LayoutParams value_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 0.3f);
		TextView tx = new TextView(context);
		tx.setText(att.get("title"));
		tx.setTextSize(11);
		tx.setTypeface(null, Typeface.BOLD);
		tx.setGravity(Gravity.LEFT);
		tx.setPadding(2, 0, 0, 2);
		tx.setTextColor(Color.rgb(66,66,66));
		container_layout.addView(tx, value_params);

		LinearLayout.LayoutParams value_one_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 0.35f);
		TextView t1 = new TextView(context);
		t1.setText(att.get("value1").equals("")?" - ":att.get("value1"));
		t1.setTextSize(11);
		t1.setGravity(Gravity.CENTER_HORIZONTAL);
		t1.setPadding(2, 0, 0, 2);
		t1.setTextColor(Color.rgb(66,66,66));
		container_layout.addView(t1, value_one_params);

		LinearLayout.LayoutParams value_two_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 0.35f);
		TextView t2 = new TextView(context);
		t2.setTextSize(11);
		t2.setText(att.get("value2").equals("")?" - ":att.get("value2"));
		t2.setGravity(Gravity.CENTER_HORIZONTAL);
		t2.setPadding(2, 0, 0, 2);
		t2.setTextColor(Color.rgb(66,66,66));
		container_layout.addView(t2, value_two_params);

		linear.addView(container_layout);
	
		return linear;
	}
}

