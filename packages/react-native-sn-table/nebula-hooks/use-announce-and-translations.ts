/* eslint-disable react-hooks/exhaustive-deps */
import {stardust, useState, useEffect} from '@nebula.js/stardust';
import registerLocale from '../locale/src';
import {AnnounceArgs} from '../types';

enum AnnouncerElements {
  FIRST = 'first-announcer-element',
  SECOND = 'second-announcer-element',
}

/* creates the function for announcement */
export const announcementFactory = (
  rootElement: Element,
  translator: stardust.Translator,
  prevAnnounceEl?: string,
) => {
  let previousAnnouncementElement = prevAnnounceEl || null;

  /* updates the aria-live elements using the translation keys, makes sure it is announced every time it is called */
  return ({
    keys,
    shouldBeAtomic = true,
    politeness = 'polite',
  }: AnnounceArgs) => {
    const notation = keys
      .map((key) => {
        if (Array.isArray(key)) {
          const [actualKey, ...rest] = key;
          return translator.get(actualKey, rest);
        }
        return translator.get(key);
      })
      .join(' ');

    const announceElement01 = rootElement.querySelector(
      '#sn-table-announcer--01',
    ) as Element;
    const announceElement02 = rootElement.querySelector(
      '#sn-table-announcer--02',
    ) as Element;

    let announceElement: Element;
    if (previousAnnouncementElement === AnnouncerElements.FIRST) {
      announceElement = announceElement02;
      previousAnnouncementElement = AnnouncerElements.SECOND;
    } else {
      announceElement = announceElement01;
      previousAnnouncementElement = AnnouncerElements.FIRST;
    }

    announceElement.innerHTML = announceElement.innerHTML.endsWith(' ­')
      ? notation
      : `${notation} ­`;
    announceElement.setAttribute('aria-atomic', shouldBeAtomic.toString());
    announceElement.setAttribute('aria-live', politeness);
  };
};

const useAnnounceAndTranslations = (
  rootElement: Element,
  translator: stardust.Translator,
) => {
  const [announce, setAnnounce] = useState<
    undefined | ((arg0: AnnounceArgs) => void)
  >(undefined);

  useEffect(() => {
    if (rootElement && translator) {
      registerLocale(translator);
      setAnnounce(() => announcementFactory(rootElement, translator));
    }
  }, [rootElement, translator.language()]);

  return announce;
};

export default useAnnounceAndTranslations;