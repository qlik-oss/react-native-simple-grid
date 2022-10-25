import { atom } from 'jotai';

export type ExpandCellAtom = {
  expand: boolean;
  data?: any;
};

export type SearchTableColumnAtom = {
  searching: boolean;
  column?: any;
};

export const expandCellAtom = atom<ExpandCellAtom>({ expand: false });

export const setExpandedCellAtom = atom(
  null,
  (_get, set, value: ExpandCellAtom) => {
    set(expandCellAtom, value);
  }
);

export const searchTableColumnAtom = atom<SearchTableColumnAtom>({
  searching: false,
});

export const setSearchingTableColumnAtom = atom(
  null,
  (_get, set, value: SearchTableColumnAtom) => {
    set(searchTableColumnAtom, value);
  }
);
