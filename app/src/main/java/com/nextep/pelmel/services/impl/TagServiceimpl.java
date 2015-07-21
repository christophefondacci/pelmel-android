package com.nextep.pelmel.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.model.Tag;
import com.nextep.pelmel.model.impl.TagImpl;
import com.nextep.pelmel.services.TagService;

public class TagServiceimpl implements TagService {

	private final Map<String, Tag> tagsMap;
	private final List<String> codesList;
	private final List<Integer> stringCodesList;
	private final List<Tag> tagsList;
	private final List<Integer> resourcesList;

	public TagServiceimpl() {
		tagsMap = new HashMap<String, Tag>();
		tagsList = new ArrayList<Tag>();
		codesList = Arrays.asList(Tag.BEAR, Tag.BEARD, Tag.BI, Tag.BLACK,
				Tag.BLOND, Tag.CHUBBY, Tag.COUPLE, Tag.CRUISING, Tag.DADDY,
				Tag.HAIRY, Tag.HARDCORE, Tag.HUGEDICK, Tag.LEATHER,
				Tag.MILITARY, Tag.MUSCLE, Tag.PIERCING, Tag.TATOO, Tag.TWINK);
		stringCodesList = Arrays.asList(R.string.tag_BEAR, R.string.tag_BEARD,
				R.string.tag_BI, R.string.tag_BLACK, R.string.tag_BLOND,
				R.string.tag_CHUBBY, R.string.tag_COUPLE,
				R.string.tag_CRUISING, R.string.tag_DADDY, R.string.tag_HAIRY,
				R.string.tag_HARDCORE, R.string.tag_HUGEDICK,
				R.string.tag_LEATHER, R.string.tag_MILITARY,
				R.string.tag_MUSCLE, R.string.tag_PIERCING,
				R.string.tag_TATTOO, R.string.tag_TWINK);
		resourcesList = Arrays.asList(R.drawable.bear, R.drawable.beard,
				R.drawable.bi, R.drawable.black, R.drawable.blond,
				R.drawable.chubby, R.drawable.couple, R.drawable.cruising,
				R.drawable.daddy, R.drawable.hairy, R.drawable.hardcore,
				R.drawable.hugedick, R.drawable.leather, R.drawable.military,
				R.drawable.muscle, R.drawable.piercing, R.drawable.tatoo,
				R.drawable.twink);

		for (final String code : codesList) {
			final Tag tag = getTag(code);
			tagsList.add(tag);
		}
	}

	@Override
	public Tag getTag(String code) {
		Tag tag = tagsMap.get(code);
		if (tag == null) {
			final int index = codesList.indexOf(code);
			if (index >= 0) {
				final Integer labelCode = stringCodesList.get(index);
				final String label = PelMelApplication.getInstance()
						.getResources().getString(labelCode);
				tag = new TagImpl(code, label);
				tagsMap.put(code, tag);
			}
		}
		return tag;
	}

	@Override
	public List<Tag> listTags() {
		return tagsList;
	}

	@Override
	public int getImageResource(Tag tag) {
		if (tag != null && tag.getCode() != null) {
			final int tagIndex = codesList.indexOf(tag.getCode());
			if (tagIndex > -1) {
				final Integer resourceId = resourcesList.get(tagIndex);
				if (resourceId != null) {
					return resourceId;
				}
			}
		}
		return R.drawable.bear;
	}
}
