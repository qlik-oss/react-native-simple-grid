import { atom } from 'jotai';

export type ExpandCellAtom = {
  expand: boolean;
  data?: any;
  titles?: Array<String>;
};

export type DragBoxAtom = {
  dragging: boolean;
};

export type SearchTableColumnAtom = {
  searching: boolean;
  column?: any;
};

export const dragBoxAtom = atom<DragBoxAtom>({ dragging: false });

export const setDragBoxAtom = atom(
  null,
  (
    _: any,
    set: (a: DragBoxAtom, v: DragBoxAtom) => void,
    value: DragBoxAtom
  ) => {
    set(dragBoxAtom, value);
  }
);

export const expandCellAtom = atom<ExpandCellAtom>({ expand: false });

export const setExpandedCellAtom = atom(
  null,
  (
    _: any,
    set: (a: ExpandCellAtom, v: ExpandCellAtom) => void,
    value: ExpandCellAtom
  ) => {
    set(expandCellAtom, value);
  }
);

export const searchTableColumnAtom = atom<SearchTableColumnAtom>({
  searching: false,
});

export const setSearchingTableColumnAtom = atom(
  null,
  (
    _: any,
    set: (a: SearchTableColumnAtom, v: SearchTableColumnAtom) => void,
    value: SearchTableColumnAtom
  ) => {
    set(searchTableColumnAtom, value);
  }
);
