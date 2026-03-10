import styled from 'styled-components'

export { Page, Form, Input, Button, TableWrapper, Actions, Table } from '../../components/ui'

export const ClickableId = styled.span`
  cursor: pointer;
  color: #0066cc;
  text-decoration: underline;

  &:hover {
    color: #004499;
  }
`

export const Section = styled.div`
  display: flex;
  flex-direction: column;
  gap: 12px;
`

export const SectionTitle = styled.h2`
  margin: 0;
  font-size: 1rem;
`
