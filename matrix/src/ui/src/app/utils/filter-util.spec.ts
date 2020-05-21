import {Filter, Filters} from 'src/app/utils/filter-util';

describe('filter-util', () => {
  describe('Filters', () => {
    let filters: Filters<string>;

    beforeEach(() => {
      filters = new Filters<string>(
        'All Strings',
        new Filter<string>('short', 'Short Strings', s => s.length < 5),
        new Filter<string>('long', 'Long Strings', s => s.length >= 5),
      );
    });

    it('selects filter', () => {
      const filter = filters.select('short');
      expect(filter.active).toBeTrue();
      expect(filter.title).toEqual('Short Strings');
    });

    it('selects null-filter when no selection is given', () => {
      const filter = filters.select(null);
      expect(filter.active).toBeFalse();
      expect(filter.title).toEqual('All Strings');
    });

    it('selects null-filter when invalid selection is given', () => {
      const filter = filters.select('snafoo');
      expect(filter.active).toBeFalse();
      expect(filter.title).toEqual('All Strings');
    });

    it('null-filter returns all items', () => {
      const filter = filters.select(null);
      const items: Array<string> = ['moo', 'mooo', 'moooo', 'mooooo'];
      expect(filter.apply(items)).toEqual(items);
    });
  });

  describe('Filter', () => {
    let filter: Filter<string>;

    beforeEach(() => {
      filter = new Filter<string>('short', 'Short Strings', s => s.length < 5);
    });

    it('handles undefined items-list', () => {
      expect(filter.apply(null)).toBeNull();
      expect(filter.apply(undefined)).toBeUndefined();
      expect(filter.apply([])).toEqual([]);
    });

    it('filters items-list', () => {
      const items: Array<string> = ['moo', 'mooo', 'moooo', 'mooooo'];
      expect(filter.apply(items)).toEqual(['moo', 'mooo']);
    });
  });
});
